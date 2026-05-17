package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.account.cmd.domain.SnapshotRepository;
import com.techbank.cqrs.domain.AggregateRoot;
import com.techbank.cqrs.events.BaseEvent;
import com.techbank.cqrs.handlers.EventSourcingHandler;
import com.techbank.cqrs.infrastructure.EventStore;
import com.techbank.cqrs.model.SnapshotModel;
import com.techbank.cqrs.producers.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AccountEventSourcingHandler
        implements EventSourcingHandler<AccountAggregate> {
    @Autowired
    private EventStore eventStore;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private SnapshotRepository snapshotRepository;

    @Override
    public void save(AggregateRoot aggregate) {
        eventStore.saveEvents(aggregate.getId(),
                aggregate.getUncommittedChanges(), aggregate.getVersion());
        aggregate.markChangesAsCommitted();

        // Kích hoạt chụp Snapshot mỗi khi version đạt bội số của 3
        if (aggregate.getVersion() > 0 && aggregate.getVersion() % 3 == 0) {
            var snapshot = SnapshotModel.builder()
                    .timestamp(new Date())
                    .aggregateIdentifier(aggregate.getId())
                    .type(aggregate.getClass().getTypeName())
                    .sequenceNumber(aggregate.getVersion())
                    .aggregateState(aggregate)
                    .build();
            snapshotRepository.save(snapshot);
        }
    }

    @Override
    public AccountAggregate getById(String id) {
        var snapshotOpt = snapshotRepository.findByAggregateIdentifier(id);
        AccountAggregate aggregate;
        long startSequenceNumber = -1;

        if (snapshotOpt.isPresent()) {
            var snapshot = snapshotOpt.get();
            aggregate = (AccountAggregate) snapshot.getAggregateState();
            startSequenceNumber = snapshot.getSequenceNumber();
        } else {
            aggregate = new AccountAggregate();
        }

        var events = eventStore.getEvents(id, startSequenceNumber);
        if (events != null && !events.isEmpty()) {
            aggregate.replayEvents(events);
            var latestVersion = events.stream()
                    .map(BaseEvent::getSequenceNumber).max(Long::compare);
            aggregate.setVersion(latestVersion.get().intValue());
        } else if (snapshotOpt.isPresent()) {
            aggregate.setVersion((int) startSequenceNumber);
        }
        return aggregate;
    }

    @Override
    public void republishEvents() {
        var aggregateIds = eventStore.getAggregateIds();
        for (var aggregateId : aggregateIds) {
            var aggregate = getById(aggregateId);
            if (aggregate == null || !aggregate.getActive()) continue;
            var events = eventStore.getEvents(aggregateId);
            for (var event : events) {
                eventProducer.produce(event.getClass().getSimpleName(), event);
            }
        }
    }
}
