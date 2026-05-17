package com.techbank.account.cmd.domain;

import com.techbank.cqrs.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "outbox_events")
public class OutboxEvent {
    @Id
    private String id;
    private String aggregateIdentifier;
    private String eventType;
    private BaseEvent payload;
    private Date timestamp;
    private String status; // PENDING, PROCESSED, FAILED
    private int attempts;
    private String traceId;
}
