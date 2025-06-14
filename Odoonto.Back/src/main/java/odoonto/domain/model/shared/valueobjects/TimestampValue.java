package odoonto.domain.model.shared.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;
import java.time.LocalDateTime;
import java.util.Objects;

@ValueObject
public final class TimestampValue {
    private final LocalDateTime value;

    private TimestampValue(final LocalDateTime value) {
        if (value == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
        this.value = value;
    }

    public static TimestampValue now() {
        return new TimestampValue(LocalDateTime.now());
    }

    public static TimestampValue of(final LocalDateTime value) {
        return new TimestampValue(value);
    }

    public LocalDateTime getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final TimestampValue that = (TimestampValue) obj;
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