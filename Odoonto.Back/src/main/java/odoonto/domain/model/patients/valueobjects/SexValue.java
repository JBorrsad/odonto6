package odoonto.domain.model.patients.valueobjects;

import java.util.Locale;

public enum SexValue {
    MALE, FEMALE, OTHER;

    public static SexValue fromString(final String s) {
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException("Sex value cannot be null or empty");
        }
        return valueOf(s.toUpperCase(Locale.ROOT));
    }

    public String getDisplayName() {
        return switch (this) {
            case MALE -> "Male";
            case FEMALE -> "Female";
            case OTHER -> "Other";
        };
    }
} 