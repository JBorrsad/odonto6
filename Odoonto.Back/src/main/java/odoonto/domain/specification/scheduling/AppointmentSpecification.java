package odoonto.domain.specification.scheduling;

import odoonto.domain.specification.shared.Specification;
import odoonto.domain.model.scheduling.aggregates.AppointmentAggregate;
import odoonto.domain.model.scheduling.valueobjects.AppointmentStatus;

import java.time.LocalDateTime;

public final class AppointmentSpecification {

    private AppointmentSpecification() {}

    public static Specification<AppointmentAggregate> isScheduled() {
        return appointment -> appointment.getStatus() == AppointmentStatus.SCHEDULED;
    }

    public static Specification<AppointmentAggregate> isCompleted() {
        return appointment -> appointment.getStatus() == AppointmentStatus.COMPLETED;
    }

    public static Specification<AppointmentAggregate> isCancelled() {
        return appointment -> appointment.getStatus() == AppointmentStatus.CANCELLED;
    }

    public static Specification<AppointmentAggregate> isToday() {
        return appointment -> {
            final LocalDateTime appointmentDateTime = appointment.getAppointmentTime().getValue();
            final LocalDateTime now = LocalDateTime.now();
            return appointmentDateTime.toLocalDate().equals(now.toLocalDate());
        };
    }

    public static Specification<AppointmentAggregate> isUpcoming() {
        return appointment -> {
            final LocalDateTime appointmentDateTime = appointment.getAppointmentTime().getValue();
            return appointmentDateTime.isAfter(LocalDateTime.now());
        };
    }

    public static Specification<AppointmentAggregate> isPast() {
        return appointment -> {
            final LocalDateTime appointmentDateTime = appointment.getAppointmentTime().getValue();
            return appointmentDateTime.isBefore(LocalDateTime.now());
        };
    }

    public static Specification<AppointmentAggregate> isTodayAndScheduled() {
        return isToday().and(isScheduled());
    }
} 