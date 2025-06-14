package odoonto.domain.model.patients.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;
import java.util.Objects;

@ValueObject
public final class PhoneNumber {
    private final String value;

    public PhoneNumber(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        final String cleanValue = value.trim().replaceAll("[^0-9+]", "");
        if (cleanValue.length() < 10) {
            throw new IllegalArgumentException("Phone number too short");
        }
        this.value = cleanValue;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final PhoneNumber that = (PhoneNumber) obj;
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