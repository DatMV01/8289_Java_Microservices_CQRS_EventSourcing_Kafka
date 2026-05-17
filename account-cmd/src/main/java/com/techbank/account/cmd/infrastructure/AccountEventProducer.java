package com.techbank.account.cmd.infrastructure;

import com.techbank.cqrs.events.BaseEvent;
import com.techbank.cqrs.producers.EventProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class AccountEventProducer implements EventProducer {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void produce(String topic, BaseEvent event) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, event);
        String traceId = MDC.get("traceId");
        if (traceId != null && !traceId.isBlank()) {
            record.headers().add("X-Trace-Id", traceId.getBytes(StandardCharsets.UTF_8));
        }
        this.kafkaTemplate.send(record);
    }
}
