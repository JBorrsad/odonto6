package odoonto.domain.model.records.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public final class ToothNumber {
    private final int value;

    public ToothNumber(final int value) {
        if (value < 1 || value > 85) {
            throw new IllegalArgumentException("ToothNumber must be between 1 and 85");
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ToothNumber that = (ToothNumber) obj;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
} 