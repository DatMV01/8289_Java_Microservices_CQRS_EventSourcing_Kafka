package com.techbank.account.query.infrastructure.consumers;

import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import com.techbank.account.query.infrastructure.projections.AccountMySQLProjection;
import com.techbank.account.query.infrastructure.projections.AccountElasticsearchProjection;
import com.techbank.account.query.infrastructure.projections.AccountRedisProjection;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountEventConsumer implements EventConsumer {
    @Autowired
    private AccountMySQLProjection accountMySQLProjection;

    @Autowired
    private AccountRedisProjection accountRedisProjection;

    @Autowired
    private AccountElasticsearchProjection accountElasticsearchProjection;

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "AccountOpenedEvent", groupId = "${spring.kafka.consumer.group-id}")
    @Override
    public void consume(@Payload AccountOpenedEvent event, 
                        @Header(value = "X-Trace-Id", required = false) String traceId,
                        Acknowledgment ack) {
        if (traceId != null && !traceId.isBlank()) {
            MDC.put("traceId", traceId);
        } else {
            MDC.put("traceId", UUID.randomUUID().toString());
        }
        try {
            this.accountMySQLProjection.on(event);
            this.accountRedisProjection.on(event);
            this.accountElasticsearchProjection.on(event);
            ack.acknowledge();
        } finally {
            MDC.clear();
        }
    }

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "FundsDepositedEvent", groupId = "${spring.kafka.consumer.group-id}")
    @Override
    public void consume(@Payload FundsDepositedEvent event, 
                        @Header(value = "X-Trace-Id", required = false) String traceId,
                        Acknowledgment ack) {
        if (traceId != null && !traceId.isBlank()) {
            MDC.put("traceId", traceId);
        } else {
            MDC.put("traceId", UUID.randomUUID().toString());
        }
        try {
            this.accountMySQLProjection.on(event);
            this.accountRedisProjection.on(event);
            this.accountElasticsearchProjection.on(event);
            ack.acknowledge();
        } finally {
            MDC.clear();
        }
    }

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "FundsWithdrawnEvent", groupId = "${spring.kafka.consumer.group-id}")
    @Override
    public void consume(@Payload FundsWithdrawnEvent event, 
                        @Header(value = "X-Trace-Id", required = false) String traceId,
                        Acknowledgment ack) {
        if (traceId != null && !traceId.isBlank()) {
            MDC.put("traceId", traceId);
        } else {
            MDC.put("traceId", UUID.randomUUID().toString());
        }
        try {
            this.accountMySQLProjection.on(event);
            this.accountRedisProjection.on(event);
            this.accountElasticsearchProjection.on(event);
            ack.acknowledge();
        } finally {
            MDC.clear();
        }
    }

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "AccountClosedEvent", groupId = "${spring.kafka.consumer.group-id}")
    @Override
    public void consume(@Payload AccountClosedEvent event, 
                        @Header(value = "X-Trace-Id", required = false) String traceId,
                        Acknowledgment ack) {
        if (traceId != null && !traceId.isBlank()) {
            MDC.put("traceId", traceId);
        } else {
            MDC.put("traceId", UUID.randomUUID().toString());
        }
        try {
            this.accountMySQLProjection.on(event);
            this.accountRedisProjection.on(event);
            this.accountElasticsearchProjection.on(event);
            ack.acknowledge();
        } finally {
            MDC.clear();
        }
    }
}
