package odoonto.domain.model.records.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;
import java.util.Objects;
import java.util.UUID;

@ValueObject
public final class MedicalRecordId {
    private final String value;

    public MedicalRecordId(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("MedicalRecordId cannot be null or empty");
        }
        this.value = value.trim();
    }

    public static MedicalRecordId generate() {
        return new MedicalRecordId(UUID.randomUUID().toString());
    }

    public static MedicalRecordId of(final String value) {
        return new MedicalRecordId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MedicalRecordId that = (MedicalRecordId) obj;
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