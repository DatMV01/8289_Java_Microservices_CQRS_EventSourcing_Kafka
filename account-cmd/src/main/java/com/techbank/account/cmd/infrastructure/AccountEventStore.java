package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.account.cmd.domain.EventStoreRepository;
import com.techbank.account.cmd.domain.OutboxEvent;
import com.techbank.account.cmd.domain.OutboxEventRepository;
import com.techbank.cqrs.events.BaseEvent;
import com.techbank.cqrs.exceptions.AggregateNotFoundException;
import com.techbank.cqrs.exceptions.ConcurrencyException;
import com.techbank.cqrs.infrastructure.EventStore;
import com.techbank.cqrs.model.EventModel;
import com.techbank.cqrs.producers.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountEventStore implements EventStore {
    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private EventStoreRepository eventStoreRepository;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Override
    public void saveEvents(String aggregateId, Iterable<BaseEvent> events, int expectedVersion) {
        var eventStream = eventStoreRepository
                .findByAggregateIdentifier(aggregateId);

        if (expectedVersion != -1
                && eventStream.get(eventStream.size() - 1).getSequenceNumber() != expectedVersion) {
            throw new ConcurrencyException();
        }

        var version = expectedVersion;
        for (var event: events) {
           version++;
           event.setSequenceNumber(version);
           var eventModel = EventModel.builder()
                   .eventIdentifier(UUID.randomUUID().toString())
                   .timestamp(new Date())
                   .aggregateIdentifier(aggregateId)
                   .type(AccountAggregate.class.getTypeName())
                   .sequenceNumber(version)
                   .payloadType(event.getClass().getTypeName())
                   .payload(event)
                   .metaData(Map.of("correlationId", UUID.randomUUID().toString()))
                   .build();

           var persistedEvent = eventStoreRepository.save(eventModel);
           if (!persistedEvent.getId().isEmpty()) {
               String traceId = org.slf4j.MDC.get("traceId");
               if (traceId == null || traceId.isBlank()) {
                   traceId = UUID.randomUUID().toString();
               }

               var outboxEvent = OutboxEvent.builder()
                       .aggregateIdentifier(aggregateId)
                       .eventType(event.getClass().getSimpleName())
                       .payload(event)
                       .timestamp(new Date())
                       .status("PENDING")
                       .attempts(0)
                       .traceId(traceId)
                       .build();

               outboxEventRepository.save(outboxEvent);
           }
        }
    }

    @Override
    public List<BaseEvent> getEvents(String aggregateId) {
        var eventStream = eventStoreRepository.findByAggregateIdentifier(aggregateId);
        if (eventStream == null || eventStream.isEmpty()) {
            throw new AggregateNotFoundException("Incorrect account ID provided!");
        }
        return eventStream.stream().map(EventModel::getPayload).collect(Collectors.toList());
    }

    @Override
    public List<BaseEvent> getEvents(String aggregateId, long fromSequenceNumber) {
        var eventStream = eventStoreRepository
                .findByAggregateIdentifierAndSequenceNumberGreaterThan(aggregateId, fromSequenceNumber);
        if (eventStream == null || eventStream.isEmpty()) {
            return List.of();
        }
        return eventStream.stream().map(EventModel::getPayload).collect(Collectors.toList());
    }


    @Override
    public List<String> getAggregateIds() {
        var eventStream = eventStoreRepository.findAll();
        if (eventStream == null || eventStream.isEmpty()) {
            throw new IllegalStateException("Could not retrieve event stream from the event store!");
        }
        return eventStream.stream().map(EventModel::getAggregateIdentifier).distinct().collect(Collectors.toList());
    }
}
