package odoonto.domain.model.records.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public final class ToothNumber {
    
    private final int number;

    private ToothNumber(final int number) {
        validateNumber(number);
        this.number = number;
    }

    public static ToothNumber of(final int number) {
        return new ToothNumber(number);
    }

    public static ToothNumber fromString(final String numberStr) {
        if (numberStr == null || numberStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Tooth number string cannot be null or empty");
        }
        
        try {
            final int number = Integer.parseInt(numberStr.trim());
            return new ToothNumber(number);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("Invalid tooth number format: " + numberStr);
        }
    }

    private static void validateNumber(final int number) {
        if (number < 1 || number > 32) {
            throw new IllegalArgumentException("Tooth number must be between 1 and 32, but was: " + number);
        }
    }

    public boolean isUpperJaw() {
        return number <= 16;
    }

    public boolean isLowerJaw() {
        return number > 16;
    }

    public boolean isIncisor() {
        return (number >= 1 && number <= 3) || 
               (number >= 6 && number <= 8) ||
               (number >= 9 && number <= 11) ||
               (number >= 14 && number <= 16) ||
               (number >= 17 && number <= 19) ||
               (number >= 22 && number <= 24) ||
               (number >= 25 && number <= 27) ||
               (number >= 30 && number <= 32);
    }

    public boolean isMolar() {
        return (number >= 4 && number <= 5) ||
               (number >= 12 && number <= 13) ||
               (number >= 20 && number <= 21) ||
               (number >= 28 && number <= 29);
    }

    public String getQuadrant() {
        if (number >= 1 && number <= 8) return "Superior Derecho";
        if (number >= 9 && number <= 16) return "Superior Izquierdo";
        if (number >= 17 && number <= 24) return "Inferior Izquierdo";
        if (number >= 25 && number <= 32) return "Inferior Derecho";
        return "Unknown";
    }

    public int getNumber() {
        return number;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final ToothNumber that = (ToothNumber) other;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }
} 