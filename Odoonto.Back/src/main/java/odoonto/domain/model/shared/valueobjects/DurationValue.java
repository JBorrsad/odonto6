package odoonto.domain.model.shared.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;
import java.util.Objects;

@ValueObject
public final class DurationValue {
    private final int minutes;

    public DurationValue(final int minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getHours() {
        return minutes / 60;
    }

    public int getRemainingMinutes() {
        return minutes % 60;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final DurationValue that = (DurationValue) obj;
        return minutes == that.minutes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minutes);
    }

    @Override
    public String toString() {
        return minutes + " minutes";
    }
} 