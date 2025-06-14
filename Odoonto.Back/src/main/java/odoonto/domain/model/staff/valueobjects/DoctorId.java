package odoonto.domain.model.staff.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public final class DoctorId {
    
    private final String value;

    private DoctorId(final String value) {
        validateValue(value);
        this.value = value;
    }

    public static DoctorId generate() {
        return new DoctorId(UUID.randomUUID().toString());
    }

    public static DoctorId of(final String value) {
        return new DoctorId(value);
    }

    private static void validateValue(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("DoctorId value cannot be null or empty");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final DoctorId doctorId = (DoctorId) other;
        return Objects.equals(value, doctorId.value);
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