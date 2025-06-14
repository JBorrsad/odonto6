package odoonto.domain.model.scheduling.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public final class AppointmentStatus {
    
    public static final AppointmentStatus SCHEDULED = new AppointmentStatus("SCHEDULED");
    public static final AppointmentStatus RESCHEDULED = new AppointmentStatus("RESCHEDULED");
    public static final AppointmentStatus COMPLETED = new AppointmentStatus("COMPLETED");
    public static final AppointmentStatus CANCELLED = new AppointmentStatus("CANCELLED");
    public static final AppointmentStatus NO_SHOW = new AppointmentStatus("NO_SHOW");
    
    private final String status;

    private AppointmentStatus(final String status) {
        validateStatus(status);
        this.status = status;
    }

    public static AppointmentStatus of(final String status) {
        validateStatus(status);
        
        return switch (status.toUpperCase()) {
            case "SCHEDULED" -> SCHEDULED;
            case "RESCHEDULED" -> RESCHEDULED;
            case "COMPLETED" -> COMPLETED;
            case "CANCELLED" -> CANCELLED;
            case "NO_SHOW" -> NO_SHOW;
            default -> throw new IllegalArgumentException("Unknown appointment status: " + status);
        };
    }

    public boolean isActive() {
        return this.equals(SCHEDULED) || this.equals(RESCHEDULED);
    }

    public boolean isCompleted() {
        return this.equals(COMPLETED);
    }

    public boolean isCancelled() {
        return this.equals(CANCELLED);
    }

    public boolean isNoShow() {
        return this.equals(NO_SHOW);
    }

    public boolean canBeModified() {
        return isActive();
    }

    public boolean canBeCompleted() {
        return isActive();
    }

    public boolean canBeCancelled() {
        return isActive();
    }

    private static void validateStatus(final String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Appointment status cannot be null or empty");
        }
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final AppointmentStatus that = (AppointmentStatus) other;
        return Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return status;
    }
} 