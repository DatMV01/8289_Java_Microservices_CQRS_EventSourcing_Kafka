package com.techbank.cqrs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@Document(collection = "snapshots")
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotModel {
    @Id
    private String id;
    private Date timestamp;
    private String aggregateIdentifier;
    private String type;
    private long sequenceNumber;
    private Object aggregateState;
}
