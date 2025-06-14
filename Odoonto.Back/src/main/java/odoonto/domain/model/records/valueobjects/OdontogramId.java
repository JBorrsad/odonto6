package odoonto.domain.model.records.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public final class OdontogramId {
    private final String value;

    public OdontogramId(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("OdontogramId cannot be null or empty");
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
        OdontogramId that = (OdontogramId) obj;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
} 