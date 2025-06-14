package odoonto.domain.model.scheduling.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;
import odoonto.domain.model.shared.valueobjects.DurationValue;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Objects;

@ValueObject
public final class AppointmentTime {
    private final LocalDateTime value;

    public AppointmentTime(final LocalDateTime value) {
        if (value == null) {
            throw new IllegalArgumentException("Appointment time cannot be null");
        }
        if (value.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment time cannot be in the past");
        }
        this.value = value;
    }

    public LocalDateTime getValue() {
        return value;
    }

    public LocalDate getDate() {
        return value.toLocalDate();
    }

    public boolean overlapsWith(final AppointmentTime other, final DurationValue duration) {
        final LocalDateTime thisEnd = this.value.plusMinutes(duration.getMinutes());
        final LocalDateTime otherEnd = other.value.plusMinutes(duration.getMinutes());
        
        return !(this.value.isEqual(otherEnd) || this.value.isAfter(otherEnd) ||
                thisEnd.isEqual(other.value) || thisEnd.isBefore(other.value));
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final AppointmentTime that = (AppointmentTime) obj;
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