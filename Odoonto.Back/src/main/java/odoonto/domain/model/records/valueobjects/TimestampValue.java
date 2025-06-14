package odoonto.domain.model.records.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import java.time.LocalDateTime;
import java.util.Objects;

@ValueObject
public final class TimestampValue {
    private final LocalDateTime value;

    public TimestampValue(final LocalDateTime value) {
        if (value == null) {
            throw new IllegalArgumentException("TimestampValue cannot be null");
        }
        this.value = value;
    }

    public static TimestampValue now() {
        return new TimestampValue(LocalDateTime.now());
    }

    public LocalDateTime getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TimestampValue that = (TimestampValue) obj;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
} 