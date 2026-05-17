package com.techbank.account.cmd.commands.handlers;

import com.techbank.account.cmd.commands.account.CloseAccountCommand;
import com.techbank.account.cmd.commands.account.OpenAccountCommand;
import com.techbank.account.cmd.commands.database.RestoreReadDbCommand;
import com.techbank.account.cmd.commands.fund.DepositFundsCommand;
import com.techbank.account.cmd.commands.fund.WithdrawFundsCommand;

public interface CommandHandler {
    void handle(OpenAccountCommand command);

    void handle(DepositFundsCommand command);

    void handle(WithdrawFundsCommand command);

    void handle(CloseAccountCommand command);

    void handle(RestoreReadDbCommand command);
}
