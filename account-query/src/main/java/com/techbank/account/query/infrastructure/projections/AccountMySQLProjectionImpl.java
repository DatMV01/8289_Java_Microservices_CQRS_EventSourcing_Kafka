package com.techbank.account.query.infrastructure.projections;

import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import com.techbank.account.query.domain.AccountRepository;
import com.techbank.account.query.domain.BankAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountMySQLProjectionImpl implements AccountMySQLProjection {
       @Autowired
    private AccountRepository accountRepository;

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
        accountRepository.save(bankAccount);
    }

    @Override
    public void on(FundsDepositedEvent event) {
        var bankAccountOpt = accountRepository.findById(event.getId());
        if (bankAccountOpt.isEmpty()) {
            return;
        }
        var bankAccount = bankAccountOpt.get();
        if (event.getSequenceNumber() <= bankAccount.getSequenceNumber()) {
            return; // Deduplication & Out-of-Order protection
        }
        var latestBalance = bankAccount.getBalance() + event.getAmount();
        bankAccount.setBalance(latestBalance);
        bankAccount.setSequenceNumber(event.getSequenceNumber());
        accountRepository.save(bankAccount);
    }

    @Override
    public void on(FundsWithdrawnEvent event) {
        var bankAccountOpt = accountRepository.findById(event.getId());
        if (bankAccountOpt.isEmpty()) {
            return;
        }
        var bankAccount = bankAccountOpt.get();
        if (event.getSequenceNumber() <= bankAccount.getSequenceNumber()) {
            return; // Deduplication & Out-of-Order protection
        }
        var latestBalance = bankAccount.getBalance() - event.getAmount();
        bankAccount.setBalance(latestBalance);
        bankAccount.setSequenceNumber(event.getSequenceNumber());
        accountRepository.save(bankAccount);
    }

    @Override
    public void on(AccountClosedEvent event) {
        var bankAccountOpt = accountRepository.findById(event.getId());
        if (bankAccountOpt.isEmpty()) {
            return;
        }
        var bankAccount = bankAccountOpt.get();
        if (event.getSequenceNumber() <= bankAccount.getSequenceNumber()) {
            return; // Deduplication & Out-of-Order protection
        }
        bankAccount.setActive(false);
        bankAccount.setSequenceNumber(event.getSequenceNumber());
        accountRepository.save(bankAccount);
    }
}
