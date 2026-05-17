package com.techbank.account.cmd;

import com.techbank.account.cmd.commands.account.CloseAccountCommand;
import com.techbank.account.cmd.commands.account.OpenAccountCommand;
import com.techbank.account.cmd.commands.database.RestoreReadDbCommand;
import com.techbank.account.cmd.commands.fund.DepositFundsCommand;
import com.techbank.account.cmd.commands.fund.WithdrawFundsCommand;
import com.techbank.account.cmd.commands.handlers.CommandHandler;
import com.techbank.cqrs.infrastructure.CommandDispatcher;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AccountCmdApplication {

    @Autowired
    private CommandDispatcher commandDispatcher;

    @Autowired
    private CommandHandler commandHandler;

    public static void main(String[] args) {
        SpringApplication.run(AccountCmdApplication.class, args);
    }

    @PostConstruct
    public void registerHandlers() {
        commandDispatcher.registerHandler(OpenAccountCommand.class, commandHandler::handle);
        commandDispatcher.registerHandler(DepositFundsCommand.class, commandHandler::handle);
        commandDispatcher.registerHandler(WithdrawFundsCommand.class, commandHandler::handle);
        commandDispatcher.registerHandler(CloseAccountCommand.class, commandHandler::handle);
        commandDispatcher.registerHandler(RestoreReadDbCommand.class, commandHandler::handle);
    }
}
