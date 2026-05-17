package com.techbank.account.cmd.infrastructure.interceptors;

import com.techbank.account.cmd.commands.fund.DepositFundsCommand;
import com.techbank.account.cmd.commands.fund.WithdrawFundsCommand;
import com.techbank.cqrs.commands.BaseCommand;
import com.techbank.cqrs.infrastructure.CommandInterceptor;
import org.springframework.stereotype.Component;

@Component
public class ValidationCommandInterceptor implements CommandInterceptor {

    @Override
    public void intercept(BaseCommand command) {
        if (command instanceof DepositFundsCommand depositCommand) {
            if (depositCommand.getAmount() <= 0) {
                throw new IllegalStateException("Validation failed: Deposit amount must be greater than 0!");
            }
        } else if (command instanceof WithdrawFundsCommand withdrawCommand) {
            if (withdrawCommand.getAmount() <= 0) {
                throw new IllegalStateException("Validation failed: Withdrawal amount must be greater than 0!");
            }
        }
    }
}
