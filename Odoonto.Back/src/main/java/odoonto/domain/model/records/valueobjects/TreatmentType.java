package odoonto.domain.model.records.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum TreatmentType {
    CONSULTATION,
    CLEANING,
    FILLING,
    EXTRACTION,
    ROOT_CANAL,
    CROWN,
    BRIDGE,
    IMPLANT,
    ORTHODONTICS,
    PERIODONTAL;

    public static TreatmentType fromString(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("TreatmentType cannot be null or empty");
        }
        return valueOf(value.toUpperCase().trim());
    }
} 