package odoonto.domain.model.scheduling.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public final class AppointmentId {
    
    private final String value;

    private AppointmentId(final String value) {
        validateValue(value);
        this.value = value;
    }

    public static AppointmentId generate() {
        return new AppointmentId(UUID.randomUUID().toString());
    }

    public static AppointmentId of(final String value) {
        return new AppointmentId(value);
    }

    private static void validateValue(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("AppointmentId value cannot be null or empty");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final AppointmentId that = (AppointmentId) other;
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