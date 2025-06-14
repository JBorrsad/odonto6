package odoonto.domain.model.shared.valueobjects;

import java.util.Objects;
import java.util.UUID;

public final class EventId {
    
    private final String id;

    private EventId(final String id) {
        validateId(id);
        this.id = id;
    }

    public static EventId generate() {
        return new EventId(UUID.randomUUID().toString());
    }

    public static EventId of(final String id) {
        return new EventId(id);
    }

    private static void validateId(final String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Event ID cannot be null or empty");
        }
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final EventId eventId = (EventId) other;
        return Objects.equals(id, eventId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }
} 