package com.techbank.cqrs.producers;

import com.techbank.cqrs.events.BaseEvent;

public interface EventProducer {
    void produce(String topic, BaseEvent event);
}
