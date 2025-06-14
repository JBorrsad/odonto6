package odoonto.domain.model.records.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import odoonto.domain.model.patients.valueobjects.PatientId;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public final class MedicalRecordId {
    
    private final String value;

    private MedicalRecordId(final String value) {
        validateValue(value);
        this.value = value;
    }

    public static MedicalRecordId generate() {
        return new MedicalRecordId(UUID.randomUUID().toString());
    }

    public static MedicalRecordId of(final String value) {
        return new MedicalRecordId(value);
    }

    public static MedicalRecordId fromPatientId(final PatientId patientId) {
        if (patientId == null) {
            throw new IllegalArgumentException("PatientId cannot be null");
        }
        return new MedicalRecordId("MR-" + patientId.getValue());
    }

    private static void validateValue(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("MedicalRecordId value cannot be null or empty");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final MedicalRecordId that = (MedicalRecordId) other;
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