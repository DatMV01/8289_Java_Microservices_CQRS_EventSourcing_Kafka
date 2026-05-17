package com.techbank.account.query.infrastructure.projections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import com.techbank.account.query.domain.BankAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AccountRedisProjectionImpl implements AccountRedisProjection {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String REDIS_KEY_PREFIX = "bank_account:";
    private static final long CACHE_TTL_DAYS = 7;

    @Override
    public void on(AccountOpenedEvent event) {
        var bankAccount = BankAccount.builder()
                .id(event.getId())
                .accountHolder(event.getAccountHolder())
                .creationDate(event.getCreatedDate())
                .accountType(event.getAccountType())
                .balance(event.getOpeningBalance())
                .isActive(true)
                .sequenceNumber(event.getSequenceNumber())
                .build();
        saveToCache(bankAccount);
    }

    @Override
    public void on(FundsDepositedEvent event) {
        BankAccount bankAccount = getFromCache(event.getId());
        if (bankAccount == null) {
            return;
        }
        if (event.getSequenceNumber() <= bankAccount.getSequenceNumber()) {
            return; // Deduplication & Out-of-Order protection
        }
        bankAccount.setBalance(bankAccount.getBalance() + event.getAmount());
        bankAccount.setSequenceNumber(event.getSequenceNumber());
        saveToCache(bankAccount);
    }

    @Override
    public void on(FundsWithdrawnEvent event) {
        BankAccount bankAccount = getFromCache(event.getId());
        if (bankAccount == null) {
            return;
        }
        if (event.getSequenceNumber() <= bankAccount.getSequenceNumber()) {
            return; // Deduplication & Out-of-Order protection
        }
        bankAccount.setBalance(bankAccount.getBalance() - event.getAmount());
        bankAccount.setSequenceNumber(event.getSequenceNumber());
        saveToCache(bankAccount);
    }

    @Override
    public void on(AccountClosedEvent event) {
        BankAccount bankAccount = getFromCache(event.getId());
        if (bankAccount == null) {
            return;
        }
        if (event.getSequenceNumber() <= bankAccount.getSequenceNumber()) {
            return; // Deduplication & Out-of-Order protection
        }
        bankAccount.setActive(false);
        bankAccount.setSequenceNumber(event.getSequenceNumber());
        saveToCache(bankAccount);
    }

    private void saveToCache(BankAccount bankAccount) {
        try {
            String json = objectMapper.writeValueAsString(bankAccount);
            redisTemplate.opsForValue().set(
                    REDIS_KEY_PREFIX + bankAccount.getId(),
                    json,
                    CACHE_TTL_DAYS,
                    TimeUnit.DAYS
            );
        } catch (Exception e) {
            System.err.println("Failed to save bank account to Redis cache: " + e.getMessage());
        }
    }

    private BankAccount getFromCache(String id) {
        try {
            String json = redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + id);
            if (json == null) {
                return null;
            }
            return objectMapper.readValue(json, BankAccount.class);
        } catch (Exception e) {
            System.err.println("Failed to read bank account from Redis cache: " + e.getMessage());
            return null;
        }
    }
}
