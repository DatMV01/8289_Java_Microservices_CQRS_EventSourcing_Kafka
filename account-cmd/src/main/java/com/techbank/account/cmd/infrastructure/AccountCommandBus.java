package com.techbank.account.cmd.infrastructure;

import com.techbank.cqrs.commands.BaseCommand;
import com.techbank.cqrs.commands.CommandHandlerMethod;
import com.techbank.cqrs.infrastructure.CommandBus;
import com.techbank.cqrs.infrastructure.CommandInterceptor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class AccountCommandBus implements CommandBus {
    private final Map<Class<? extends BaseCommand>, List<CommandHandlerMethod>> routes = new HashMap<>();
    private final List<CommandInterceptor> interceptors = new LinkedList<>();

    @Override
    public <T extends BaseCommand> void registerHandler(Class<T> type, CommandHandlerMethod<T> handler) {
        var handlers = routes.computeIfAbsent(type, k -> new LinkedList<>());
        handlers.add(handler);
    }

    @Override
    public void registerInterceptor(CommandInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }

    @Override
    public void dispatch(BaseCommand command) {
        for (var interceptor : interceptors) {
            interceptor.intercept(command);
        }

        var handlers = routes.get(command.getClass());
        if (handlers == null || handlers.isEmpty()) {
            throw new RuntimeException("No command handler was registered!");
        }
        if (handlers.size() > 1) {
            throw new RuntimeException("Cannot dispatch command to more than one handler!");
        }
        handlers.get(0).handle(command);
    }
}
