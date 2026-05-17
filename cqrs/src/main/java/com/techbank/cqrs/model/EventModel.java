package com.techbank.cqrs.model;

import com.techbank.cqrs.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Data
@Builder
@Document(collection = "events")
@NoArgsConstructor
@AllArgsConstructor
public class EventModel {
    @Id
    private String id;
    private String eventIdentifier;
    private Date timestamp;
    private String aggregateIdentifier;
    private String type;
    private long sequenceNumber;
    private String payloadType;
    private BaseEvent payload;
    private Map<String, Object> metaData;
}
