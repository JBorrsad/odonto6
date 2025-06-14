package odoonto.domain.model.scheduling.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;
import java.util.Objects;
import java.util.UUID;

@ValueObject
public final class AppointmentId {
    private final String value;

    private AppointmentId(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("AppointmentId cannot be null or empty");
        }
        this.value = value.trim();
    }

    public static AppointmentId generate() {
        return new AppointmentId(UUID.randomUUID().toString());
    }

    public static AppointmentId of(final String value) {
        return new AppointmentId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final AppointmentId that = (AppointmentId) obj;
        return Objects.equals(value, that.value);
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