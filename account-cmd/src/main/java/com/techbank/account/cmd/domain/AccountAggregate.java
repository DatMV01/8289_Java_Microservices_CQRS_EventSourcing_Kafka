package com.techbank.account.cmd.domain;

import com.techbank.account.cmd.commands.account.OpenAccountCommand;
import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import com.techbank.cqrs.annotations.EventSourcingHandler;
import com.techbank.cqrs.domain.AggregateRoot;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
public class AccountAggregate extends AggregateRoot {
    private Boolean active;
    private double balance;

    public AccountAggregate(OpenAccountCommand command) {
        raiseEvent(AccountOpenedEvent.builder()
                .id(command.getId())
                .accountHolder(command.getAccountHolder())
                .createdDate(new Date())
                .accountType(command.getAccountType())
                .openingBalance(command.getOpeningBalance())
                .build());
    }

    public double getBalance() {
        return this.balance;
    }

    public Boolean getActive() {
        return this.active;
    }

    @EventSourcingHandler
    public void on(AccountOpenedEvent event) {
        this.id = event.getId();
        this.active = true;
        this.balance = event.getOpeningBalance();
    }

    public void depositFunds(double amount) {
        if (!this.active) {
            throw new IllegalStateException("Funds cannot be deposited into a closed account!");
        }
        if (amount <= 0) {
            throw new IllegalStateException("The deposit amount must be greater than 0!");
        }
        raiseEvent(FundsDepositedEvent.builder()
                .id(this.id)
                .amount(amount)
                .build());
    }

    @EventSourcingHandler
    public void on(FundsDepositedEvent event) {
        this.id = event.getId();
        this.balance += event.getAmount();
    }

    public void withdrawFunds(double amount) {
        if (!this.active) {
            throw new IllegalStateException("Funds cannot be withdrawn from a closed account!");
        }
        raiseEvent(FundsWithdrawnEvent.builder()
                .id(this.id)
                .amount(amount)
                .build());
    }

    @EventSourcingHandler
    public void on(FundsWithdrawnEvent event) {
        this.id = event.getId();
        this.balance -= event.getAmount();
    }

    public void closeAccount() {
        if (!this.active) {
            throw new IllegalStateException("The bank account has already been closed!");
        }
        raiseEvent(AccountClosedEvent.builder()
                .id(this.id)
                .build());
    }

    @EventSourcingHandler
    public void on(AccountClosedEvent event) {
        this.id = event.getId();
        this.active = false;
    }
}
