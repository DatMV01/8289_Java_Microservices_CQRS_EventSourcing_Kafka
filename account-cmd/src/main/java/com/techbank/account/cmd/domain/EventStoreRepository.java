package com.techbank.account.cmd.domain;

import com.techbank.cqrs.model.EventModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventStoreRepository extends MongoRepository<EventModel, String> {
    List<EventModel> findByAggregateIdentifier(String aggregateIdentifier);
    List<EventModel> findByAggregateIdentifierAndSequenceNumberGreaterThan(String aggregateIdentifier, long sequenceNumber);
}
