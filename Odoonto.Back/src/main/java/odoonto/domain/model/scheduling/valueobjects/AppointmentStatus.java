package odoonto.domain.model.scheduling.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;
import java.util.Objects;

@ValueObject
public final class AppointmentStatus {
    public enum Status {
        SCHEDULED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    private final Status value;

    public AppointmentStatus(final Status value) {
        if (value == null) {
            throw new IllegalArgumentException("Appointment status cannot be null");
        }
        this.value = value;
    }

    public Status getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final AppointmentStatus that = (AppointmentStatus) obj;
        return value == that.value;
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