package com.techbank.account.query.domain;

import com.techbank.account.common.dto.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "bank_accounts")
public class BankAccountDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String accountHolder;

    @Field(type = FieldType.Date)
    private Date creationDate;

    @Field(type = FieldType.Keyword)
    private AccountType accountType;

    @Field(type = FieldType.Double)
    private double balance;

    @Field(type = FieldType.Boolean)
    private boolean isActive;

    @Field(type = FieldType.Long)
    private long sequenceNumber;
}
