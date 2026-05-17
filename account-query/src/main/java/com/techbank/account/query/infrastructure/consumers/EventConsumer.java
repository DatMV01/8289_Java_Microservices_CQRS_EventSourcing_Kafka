package com.techbank.account.query.infrastructure.consumers;

import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

public interface EventConsumer {
    void consume(@Payload AccountOpenedEvent event, @Header(value = "X-Trace-Id", required = false) String traceId, Acknowledgment ack);

    void consume(@Payload FundsDepositedEvent event, @Header(value = "X-Trace-Id", required = false) String traceId, Acknowledgment ack);

    void consume(@Payload FundsWithdrawnEvent event, @Header(value = "X-Trace-Id", required = false) String traceId, Acknowledgment ack);

    void consume(@Payload AccountClosedEvent event, @Header(value = "X-Trace-Id", required = false) String traceId, Acknowledgment ack);
}
