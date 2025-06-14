package odoonto.domain.model.catalog.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public final class TreatmentId {
    
    private final String value;

    private TreatmentId(final String value) {
        validateValue(value);
        this.value = value;
    }

    public static TreatmentId generate() {
        return new TreatmentId(UUID.randomUUID().toString());
    }

    public static TreatmentId of(final String value) {
        return new TreatmentId(value);
    }

    private static void validateValue(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("TreatmentId value cannot be null or empty");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final TreatmentId that = (TreatmentId) other;
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