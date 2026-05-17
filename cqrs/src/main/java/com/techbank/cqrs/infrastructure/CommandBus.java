package com.techbank.cqrs.infrastructure;

import com.techbank.cqrs.commands.BaseCommand;
import com.techbank.cqrs.commands.CommandHandlerMethod;

public interface CommandBus {
    <T extends BaseCommand> void registerHandler(Class<T> type, CommandHandlerMethod<T> handler);
    void dispatch(BaseCommand command);
    void registerInterceptor(CommandInterceptor interceptor);
}
