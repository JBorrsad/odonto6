package odoonto.domain.model.patients.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

@ValueObject
public final class BirthDateValue {
    private final LocalDate value;

    public BirthDateValue(final LocalDate value) {
        if (value == null) {
            throw new IllegalArgumentException("Birth date cannot be null");
        }
        if (value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
        if (value.isBefore(LocalDate.now().minusYears(150))) {
            throw new IllegalArgumentException("Birth date cannot be more than 150 years ago");
        }
        this.value = value;
    }

    public LocalDate getValue() {
        return value;
    }

    public int getAge() {
        return Period.between(value, LocalDate.now()).getYears();
    }

    public boolean isMinor() {
        return getAge() < 18;
    }

    public boolean isSenior() {
        return getAge() >= 65;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final BirthDateValue that = (BirthDateValue) obj;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
} 