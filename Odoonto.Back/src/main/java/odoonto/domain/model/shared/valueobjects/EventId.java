package odoonto.domain.model.shared.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;
import java.util.Objects;
import java.util.UUID;

@ValueObject
public final class EventId {
    private final String value;

    private EventId(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("EventId cannot be null or empty");
        }
        this.value = value.trim();
    }

    public static EventId generate() {
        return new EventId(UUID.randomUUID().toString());
    }

    public static EventId of(final String value) {
        return new EventId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final EventId eventId = (EventId) obj;
        return Objects.equals(value, eventId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
} 