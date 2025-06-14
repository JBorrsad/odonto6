package odoonto.domain.model.catalog.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public final class CatalogId {
    private final String value;

    public CatalogId(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("CatalogId cannot be null or empty");
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
        CatalogId catalogId = (CatalogId) obj;
        return Objects.equals(value, catalogId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
} 