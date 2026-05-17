package com.techbank.account.query.infrastructure.projections;

import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import com.techbank.account.query.domain.BankAccountDocument;
import com.techbank.account.query.domain.BankAccountElasticsearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountElasticsearchProjectionImpl implements AccountElasticsearchProjection  {
    @Autowired
    private BankAccountElasticsearchRepository elasticsearchRepository;

    @Override
    public void on(AccountOpenedEvent event) {
        var doc = BankAccountDocument.builder()
                .id(event.getId())
                .accountHolder(event.getAccountHolder())
                .creationDate(event.getCreatedDate())
                .accountType(event.getAccountType())
                .balance(event.getOpeningBalance())
                .isActive(true)
                .sequenceNumber(event.getSequenceNumber())
                .build();
        saveToElasticsearch(doc);
    }

    @Override
    public void on(FundsDepositedEvent event) {
        BankAccountDocument doc = getFromElasticsearch(event.getId());
        if (doc == null) {
            return;
        }
        if (event.getSequenceNumber() <= doc.getSequenceNumber()) {
            return; // Deduplication & Out-of-Order protection
        }
        doc.setBalance(doc.getBalance() + event.getAmount());
        doc.setSequenceNumber(event.getSequenceNumber());
        saveToElasticsearch(doc);
    }

    @Override
    public void on(FundsWithdrawnEvent event) {
        BankAccountDocument doc = getFromElasticsearch(event.getId());
        if (doc == null) {
            return;
        }
        if (event.getSequenceNumber() <= doc.getSequenceNumber()) {
            return; // Deduplication & Out-of-Order protection
        }
        doc.setBalance(doc.getBalance() - event.getAmount());
        doc.setSequenceNumber(event.getSequenceNumber());
        saveToElasticsearch(doc);
    }

    @Override
    public void on(AccountClosedEvent event) {
        BankAccountDocument doc = getFromElasticsearch(event.getId());
        if (doc == null) {
            return;
        }
        if (event.getSequenceNumber() <= doc.getSequenceNumber()) {
            return; // Deduplication & Out-of-Order protection
        }
        doc.setActive(false);
        doc.setSequenceNumber(event.getSequenceNumber());
        saveToElasticsearch(doc);
    }

    private void saveToElasticsearch(BankAccountDocument doc) {
        try {
            elasticsearchRepository.save(doc);
        } catch (Exception e) {
            System.err.println("Failed to index bank account to Elasticsearch: " + e.getMessage());
        }
    }

    private BankAccountDocument getFromElasticsearch(String id) {
        try {
            return elasticsearchRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Failed to retrieve bank account from Elasticsearch: " + e.getMessage());
            return null;
        }
    }
}
