package com.techbank.cqrs.domain;

import com.techbank.cqrs.annotations.EventSourcingHandler;
import com.techbank.cqrs.events.BaseEvent;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AggregateRoot {
    private final List<BaseEvent> changes = new ArrayList<>();
    private final Logger logger = Logger.getLogger(AggregateRoot.class.getName());
    protected String id;
    private int version = -1;

    public String getId() {
        return this.id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<BaseEvent> getUncommittedChanges() {
        return this.changes;
    }

    public void markChangesAsCommitted() {
        this.changes.clear();
    }

    protected void applyChange(BaseEvent event, Boolean isNewEvent) {
        try {
            Method targetMethod = null;
            
            // 1. Scan for methods carrying @EventSourcingHandler annotation taking this event type
            for (Method method : getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(EventSourcingHandler.class)) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 1 && parameterTypes[0].equals(event.getClass())) {
                        targetMethod = method;
                        break;
                    }
                }
            }

            // 2. Backward-compatible fallback to search for classical 'apply' method name
            if (targetMethod == null) {
                try {
                    targetMethod = getClass().getDeclaredMethod("apply", event.getClass());
                } catch (NoSuchMethodException ignored) {
                }
            }

            if (targetMethod != null) {
                targetMethod.setAccessible(true);
                targetMethod.invoke(this, event);
            } else {
                logger.log(Level.WARNING, MessageFormat.format("No event sourcing handler method found in aggregate for event: {0}", event.getClass().getName()));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error applying event to aggregate", e);
        } finally {
            if (isNewEvent) {
                changes.add(event);
            }
        }
    }

    public void raiseEvent(BaseEvent event) {
        applyChange(event, true);
    }

    public void replayEvents(Iterable<BaseEvent> events) {
        events.forEach(event -> applyChange(event, false));
    }
}
