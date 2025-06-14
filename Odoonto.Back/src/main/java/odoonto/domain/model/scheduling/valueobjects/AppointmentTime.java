package odoonto.domain.model.scheduling.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@ValueObject
public final class AppointmentTime {
    
    private final LocalDate date;
    private final LocalTime time;

    private AppointmentTime(final LocalDate date, final LocalTime time) {
        validateDateTime(date, time);
        this.date = date;
        this.time = time;
    }

    public static AppointmentTime of(final LocalDate date, final LocalTime time) {
        return new AppointmentTime(date, time);
    }

    public static AppointmentTime of(final LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("DateTime cannot be null");
        }
        return new AppointmentTime(dateTime.toLocalDate(), dateTime.toLocalTime());
    }

    public static AppointmentTime fromString(final String dateStr, final String timeStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }
        if (timeStr == null || timeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Time string cannot be null or empty");
        }
        
        try {
            final LocalDate parsedDate = LocalDate.parse(dateStr.trim());
            final LocalTime parsedTime = LocalTime.parse(timeStr.trim());
            return new AppointmentTime(parsedDate, parsedTime);
        } catch (final Exception e) {
            throw new IllegalArgumentException("Invalid date/time format: " + dateStr + " " + timeStr, e);
        }
    }

    public boolean isBefore(final AppointmentTime other) {
        if (other == null) {
            throw new IllegalArgumentException("Other appointment time cannot be null");
        }
        return toLocalDateTime().isBefore(other.toLocalDateTime());
    }

    public boolean isAfter(final AppointmentTime other) {
        if (other == null) {
            throw new IllegalArgumentException("Other appointment time cannot be null");
        }
        return toLocalDateTime().isAfter(other.toLocalDateTime());
    }

    public boolean isToday() {
        return date.equals(LocalDate.now());
    }

    public boolean isInPast() {
        return toLocalDateTime().isBefore(LocalDateTime.now());
    }

    public boolean isInFuture() {
        return toLocalDateTime().isAfter(LocalDateTime.now());
    }

    public boolean isWorkingHours() {
        final int hour = time.getHour();
        return hour >= 8 && hour < 18;
    }

    public boolean isOnWeekend() {
        return date.getDayOfWeek().getValue() >= 6;
    }

    public LocalDateTime toLocalDateTime() {
        return LocalDateTime.of(date, time);
    }

    public String formatForDisplay() {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " +
               time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String formatDateOnly() {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String formatTimeOnly() {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private static void validateDateTime(final LocalDate date, final LocalTime time) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (time == null) {
            throw new IllegalArgumentException("Time cannot be null");
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final AppointmentTime that = (AppointmentTime) other;
        return Objects.equals(date, that.date) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, time);
    }

    @Override
    public String toString() {
        return formatForDisplay();
    }
} 