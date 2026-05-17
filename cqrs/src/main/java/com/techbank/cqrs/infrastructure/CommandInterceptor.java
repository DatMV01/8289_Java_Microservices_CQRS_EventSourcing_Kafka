package com.techbank.cqrs.infrastructure;

import com.techbank.cqrs.commands.BaseCommand;

@FunctionalInterface
public interface CommandInterceptor {
    void intercept(BaseCommand command);
}
