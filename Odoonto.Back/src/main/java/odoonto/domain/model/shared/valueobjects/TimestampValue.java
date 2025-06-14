package odoonto.domain.model.shared.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@ValueObject
public final class TimestampValue {
    
    private final LocalDateTime timestamp;

    private TimestampValue(final LocalDateTime timestamp) {
        validateTimestamp(timestamp);
        this.timestamp = timestamp;
    }

    public static TimestampValue now() {
        return new TimestampValue(LocalDateTime.now());
    }

    public static TimestampValue of(final LocalDateTime timestamp) {
        return new TimestampValue(timestamp);
    }

    public static TimestampValue fromString(final String timestampStr) {
        if (timestampStr == null || timestampStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Timestamp string cannot be null or empty");
        }
        
        try {
            final LocalDateTime parsed = LocalDateTime.parse(timestampStr.trim());
            return new TimestampValue(parsed);
        } catch (final Exception e) {
            throw new IllegalArgumentException("Invalid timestamp format: " + timestampStr, e);
        }
    }

    public boolean isBefore(final TimestampValue other) {
        if (other == null) {
            throw new IllegalArgumentException("Other timestamp cannot be null");
        }
        return this.timestamp.isBefore(other.timestamp);
    }

    public boolean isAfter(final TimestampValue other) {
        if (other == null) {
            throw new IllegalArgumentException("Other timestamp cannot be null");
        }
        return this.timestamp.isAfter(other.timestamp);
    }

    public boolean isToday() {
        final LocalDateTime now = LocalDateTime.now();
        return timestamp.toLocalDate().equals(now.toLocalDate());
    }

    public boolean isInPast() {
        return timestamp.isBefore(LocalDateTime.now());
    }

    public boolean isInFuture() {
        return timestamp.isAfter(LocalDateTime.now());
    }

    public String formatForDisplay() {
        return timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String formatForPersistence() {
        return timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public int compareTo(final TimestampValue other) {
        if (other == null) {
            throw new IllegalArgumentException("Other timestamp cannot be null");
        }
        return this.timestamp.compareTo(other.timestamp);
    }

    private static void validateTimestamp(final LocalDateTime timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final TimestampValue that = (TimestampValue) other;
        return Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }

    @Override
    public String toString() {
        return formatForDisplay();
    }
} 