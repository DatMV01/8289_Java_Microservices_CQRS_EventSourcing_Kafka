package com.techbank.account.query.infrastructure.handlers;

import com.techbank.account.query.api.dto.EqualityType;
import com.techbank.account.query.api.queries.FindAccountByHolderQuery;
import com.techbank.account.query.api.queries.FindAccountByIdQuery;
import com.techbank.account.query.api.queries.FindAccountWithBalanceQuery;
import com.techbank.account.query.api.queries.FindAllAccountsQuery;
import com.techbank.account.query.domain.AccountRepository;
import com.techbank.account.query.domain.BankAccount;
import com.techbank.cqrs.domain.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles query requests for account read models using the account repository.
 *
 * Converts repository results into lists of BaseEntity responses for the query bus.
 */
@Service
public class AccountQueryHandler implements QueryHandler {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    /**
     * Handle a query for all accounts and return a list of bank account entities.
     */
    public List<BaseEntity> handle(FindAllAccountsQuery query) {
        Iterable<BankAccount> bankAccounts = accountRepository.findAll();
        List<BaseEntity> bankAccountsList = new ArrayList<>();
        bankAccounts.forEach(bankAccountsList::add);
        return bankAccountsList;
    }

    @Override
    public List<BaseEntity> handle(FindAccountByIdQuery query) {
        var bankAccount = accountRepository.findById(query.getId());
        if (bankAccount.isEmpty()) {
            return null;
        }
        List<BaseEntity> bankAccountList = new ArrayList<>();
        bankAccountList.add(bankAccount.get());
        return bankAccountList;
    }

    @Override
    /**
     * Handle a query by account holder name and return matches as entities.
     */
    public List<BaseEntity> handle(FindAccountByHolderQuery query) {
        var bankAccount = accountRepository.findByAccountHolder(query.getAccountHolder());
        if (bankAccount.isEmpty()) {
            return null;
        }
        List<BaseEntity> bankAccountList = new ArrayList<>();
        bankAccountList.add(bankAccount.get());
        return bankAccountList;
    }

    @Override
    /**
     * Handle a balance lookup query by returning accounts matching the requested criteria.
     */
    public List<BaseEntity> handle(FindAccountWithBalanceQuery query) {
        List<BaseEntity> bankAccountsList = query.getEqualityType() == EqualityType.GREATER_THAN
                ? accountRepository.findByBalanceGreaterThan(query.getBalance())
                : accountRepository.findByBalanceLessThan(query.getBalance());
        return bankAccountsList;
    }
}
