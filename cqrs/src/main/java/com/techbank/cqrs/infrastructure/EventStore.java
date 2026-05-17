package com.techbank.cqrs.infrastructure;

import com.techbank.cqrs.events.BaseEvent;

import java.util.List;

public interface EventStore {
    void saveEvents(String aggregateId, Iterable<BaseEvent> events, int expectedVersion);

    List<BaseEvent> getEvents(String aggregateId);

    List<BaseEvent> getEvents(String aggregateId, long fromSequenceNumber);

    List<String> getAggregateIds();
}
