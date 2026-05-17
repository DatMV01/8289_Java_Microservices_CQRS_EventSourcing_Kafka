package com.techbank.account.query;

import com.techbank.account.query.api.queries.*;
import com.techbank.account.query.infrastructure.handlers.QueryHandler;
import com.techbank.cqrs.infrastructure.QueryBus;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AccountQueryApplication {
    @Autowired
    private QueryBus queryBus;

    @Autowired
    private QueryHandler queryHandler;

    public static void main(String[] args) {
        SpringApplication.run(AccountQueryApplication.class, args);
    }

    @PostConstruct
    public void registerHandlers() {
        queryBus.registerHandler(FindAllAccountsQuery.class, queryHandler::handle);
        queryBus.registerHandler(FindAccountByIdQuery.class, queryHandler::handle);
        queryBus.registerHandler(FindAccountByHolderQuery.class, queryHandler::handle);
        queryBus.registerHandler(FindAccountWithBalanceQuery.class, queryHandler::handle);
    }
}
