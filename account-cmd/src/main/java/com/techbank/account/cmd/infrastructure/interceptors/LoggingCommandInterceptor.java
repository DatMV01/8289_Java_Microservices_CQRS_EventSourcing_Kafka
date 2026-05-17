package com.techbank.account.cmd.infrastructure.interceptors;

import com.techbank.cqrs.commands.BaseCommand;
import com.techbank.cqrs.infrastructure.CommandInterceptor;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class LoggingCommandInterceptor implements CommandInterceptor {
    private final Logger logger = Logger.getLogger(LoggingCommandInterceptor.class.getName());

    @Override
    public void intercept(BaseCommand command) {
        logger.info("Command Dispatched: " + command.getClass().getSimpleName() + " with Aggregate ID: " + command.getId());
    }
}
