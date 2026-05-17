package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.domain.OutboxEvent;
import com.techbank.account.cmd.domain.OutboxEventRepository;
import com.techbank.cqrs.producers.EventProducer;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OutboxPublisher {
    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private EventProducer eventProducer;

    @Scheduled(fixedDelay = 500)
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatus("PENDING");
        if (pendingEvents == null || pendingEvents.isEmpty()) {
            return;
        }

        for (OutboxEvent event : pendingEvents) {
            try {
                if (event.getTraceId() != null && !event.getTraceId().isBlank()) {
                    MDC.put("traceId", event.getTraceId());
                } else {
                    MDC.put("traceId", UUID.randomUUID().toString());
                }

                eventProducer.produce(event.getEventType(), event.getPayload());

                event.setStatus("PROCESSED");
                outboxEventRepository.save(event);
            } catch (Exception e) {
                System.err.println("Failed to publish outbox event: " + e.getMessage());
                event.setAttempts(event.getAttempts() + 1);
                if (event.getAttempts() >= 5) {
                    event.setStatus("FAILED");
                }
                outboxEventRepository.save(event);
            } finally {
                MDC.clear();
            }
        }
    }
}
