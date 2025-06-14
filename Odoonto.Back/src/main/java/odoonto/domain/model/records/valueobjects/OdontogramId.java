package odoonto.domain.model.records.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import odoonto.domain.model.patients.valueobjects.PatientId;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public final class OdontogramId {
    
    private final String value;

    private OdontogramId(final String value) {
        validateValue(value);
        this.value = value;
    }

    public static OdontogramId generate() {
        return new OdontogramId(UUID.randomUUID().toString());
    }

    public static OdontogramId of(final String value) {
        return new OdontogramId(value);
    }

    public static OdontogramId fromPatientId(final PatientId patientId) {
        if (patientId == null) {
            throw new IllegalArgumentException("PatientId cannot be null");
        }
        return new OdontogramId("ODO-" + patientId.getValue());
    }

    private static void validateValue(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("OdontogramId value cannot be null or empty");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final OdontogramId that = (OdontogramId) other;
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