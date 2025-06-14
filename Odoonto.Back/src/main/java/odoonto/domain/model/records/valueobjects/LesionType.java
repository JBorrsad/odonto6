package odoonto.domain.model.records.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum LesionType {
    CARIES,
    FRACTURE,
    RESTORATION,
    EXTRACTION,
    ENDODONTIC,
    PERIODONTAL;

    public static LesionType fromString(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("LesionType cannot be null or empty");
        }
        return valueOf(value.toUpperCase().trim());
    }
} 