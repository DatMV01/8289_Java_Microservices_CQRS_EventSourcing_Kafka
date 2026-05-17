package com.techbank.account.cmd;

import com.techbank.account.cmd.commands.account.CloseAccountCommand;
import com.techbank.account.cmd.commands.account.OpenAccountCommand;
import com.techbank.account.cmd.commands.database.RestoreReadDbCommand;
import com.techbank.account.cmd.commands.fund.DepositFundsCommand;
import com.techbank.account.cmd.commands.fund.WithdrawFundsCommand;
import com.techbank.account.cmd.commands.handlers.CommandHandler;
import com.techbank.account.cmd.infrastructure.interceptors.LoggingCommandInterceptor;
import com.techbank.account.cmd.infrastructure.interceptors.ValidationCommandInterceptor;
import com.techbank.cqrs.infrastructure.CommandBus;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AccountCmdApplication {

    @Autowired
    private CommandBus commandBus;

    @Autowired
    private CommandHandler commandHandler;

    @Autowired
    private LoggingCommandInterceptor loggingCommandInterceptor;

    @Autowired
    private ValidationCommandInterceptor validationCommandInterceptor;

    public static void main(String[] args) {
        SpringApplication.run(AccountCmdApplication.class, args);
    }

    @PostConstruct
    public void registerHandlers() {
        commandBus.registerInterceptor(loggingCommandInterceptor);
        commandBus.registerInterceptor(validationCommandInterceptor);

        commandBus.registerHandler(OpenAccountCommand.class, commandHandler::handle);
        commandBus.registerHandler(DepositFundsCommand.class, commandHandler::handle);
        commandBus.registerHandler(WithdrawFundsCommand.class, commandHandler::handle);
        commandBus.registerHandler(CloseAccountCommand.class, commandHandler::handle);
        commandBus.registerHandler(RestoreReadDbCommand.class, commandHandler::handle);
    }
}
