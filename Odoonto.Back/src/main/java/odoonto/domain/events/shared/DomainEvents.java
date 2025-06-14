package odoonto.domain.events.shared;

import java.util.ArrayList;
import java.util.List;

public final class DomainEvents {
    private static final ThreadLocal<List<DomainEvent>> events = new ThreadLocal<>();

    private DomainEvents() {}

    public static void raise(final DomainEvent event) {
        if (events.get() == null) {
            events.set(new ArrayList<>());
        }
        events.get().add(event);
    }

    public static List<DomainEvent> getEvents() {
        if (events.get() == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(events.get());
    }

    public static void clear() {
        if (events.get() != null) {
            events.get().clear();
        }
    }
} 