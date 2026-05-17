package com.techbank.account.query.infrastructure;

import com.techbank.cqrs.domain.BaseEntity;
import com.techbank.cqrs.infrastructure.QueryBus;
import com.techbank.cqrs.queries.BaseQuery;
import com.techbank.cqrs.queries.QueryHandlerMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Query bus implementation for the account query side.
 *
 * Maps queries to a single handler and returns query results for read models.
 */
@Service
public class AccountQueryBus implements QueryBus {
    /** Registered query handlers keyed by query class. */
    private final Map<Class<? extends BaseQuery>, List<QueryHandlerMethod>> routes = new HashMap<>();

    @Override
    public <T extends BaseQuery> void registerHandler(Class<T> type, QueryHandlerMethod<T> handler) {
        var handlers = routes.computeIfAbsent(type, k -> new LinkedList<>());
        handlers.add(handler);
    }

    /**
     * Dispatch the query to its single registered handler and return the result.
     */
    @Override
    public <U extends BaseEntity> List<U> query(BaseQuery query) {
        var handlers = routes.get(query.getClass());
        if (handlers == null || handlers.isEmpty()) {
            throw new RuntimeException("No query handler was registered!");
        }
        if (handlers.size() > 1) {
            throw new RuntimeException("Cannot dispatch query to more than one handler!");
        }
        return handlers.get(0).handle(query);
    }
}
