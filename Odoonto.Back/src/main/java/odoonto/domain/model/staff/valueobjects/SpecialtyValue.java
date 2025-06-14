package odoonto.domain.model.staff.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum SpecialtyValue {
    GENERAL_DENTISTRY,
    ORTHODONTICS,
    ENDODONTICS,
    PERIODONTICS,
    ORAL_SURGERY,
    PEDIATRIC_DENTISTRY,
    PROSTHODONTICS,
    ORAL_PATHOLOGY;

    public static SpecialtyValue fromString(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("SpecialtyValue cannot be null or empty");
        }
        return valueOf(value.toUpperCase().trim());
    }
} 