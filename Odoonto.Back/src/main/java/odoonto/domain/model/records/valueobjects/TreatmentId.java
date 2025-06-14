package odoonto.domain.model.records.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public final class TreatmentId {
    private final String value;

    public TreatmentId(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("TreatmentId cannot be null or empty");
        }
        this.value = value.trim();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TreatmentId that = (TreatmentId) obj;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
} 