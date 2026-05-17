package com.techbank.account.cmd.domain;

import com.techbank.cqrs.model.SnapshotModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SnapshotRepository extends MongoRepository<SnapshotModel, String> {
    Optional<SnapshotModel> findByAggregateIdentifier(String aggregateIdentifier);
}
