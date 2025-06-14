package odoonto.domain.model.patients.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;
import java.util.Objects;
import java.util.UUID;

@ValueObject
public final class PatientId {
    private final String value;

    private PatientId(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("PatientId cannot be null or empty");
        }
        this.value = value.trim();
    }

    public static PatientId generate() {
        return new PatientId(UUID.randomUUID().toString());
    }

    public static PatientId of(final String value) {
        return new PatientId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final PatientId patientId = (PatientId) obj;
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