package odoonto.domain.model.patients.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public final class PatientId {
    
    private final String value;

    private PatientId(final String value) {
        validateValue(value);
        this.value = value;
    }

    public static PatientId generate() {
        return new PatientId(UUID.randomUUID().toString());
    }

    public static PatientId of(final String value) {
        return new PatientId(value);
    }

    private static void validateValue(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("PatientId value cannot be null or empty");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final PatientId patientId = (PatientId) other;
        return Objects.equals(value, patientId.value);
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