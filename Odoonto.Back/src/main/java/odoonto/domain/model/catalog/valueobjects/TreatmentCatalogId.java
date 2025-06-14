package odoonto.domain.model.catalog.valueobjects;

import java.util.Objects;
import java.util.UUID;

public final class TreatmentCatalogId {
    
    private final String value;

    private TreatmentCatalogId(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("TreatmentCatalogId cannot be null or empty");
        }
        this.value = value;
    }

    public static TreatmentCatalogId generate() {
        return new TreatmentCatalogId(UUID.randomUUID().toString());
    }

    public static TreatmentCatalogId of(final String value) {
        return new TreatmentCatalogId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final TreatmentCatalogId that = (TreatmentCatalogId) other;
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