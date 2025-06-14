package odoonto.domain.model.records.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum DentitionType {
    TEMPORARY,
    PERMANENT,
    MIXED;

    public static DentitionType fromString(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("DentitionType cannot be null or empty");
        }
        return valueOf(value.toUpperCase().trim());
    }
} 