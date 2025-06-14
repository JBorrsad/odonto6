package odoonto.domain.model.scheduling.aggregates;

import org.jmolecules.ddd.annotation.AggregateRoot;
import odoonto.domain.model.scheduling.valueobjects.*;
import odoonto.domain.model.patients.valueobjects.PatientId;
import odoonto.domain.model.staff.valueobjects.DoctorId;
import odoonto.domain.model.shared.valueobjects.MoneyValue;
import odoonto.domain.model.shared.valueobjects.DurationValue;
import java.util.Objects;

@AggregateRoot
public class AppointmentAggregate {
    private final AppointmentId appointmentId;
    private final PatientId patientId;
    private final DoctorId doctorId;
    private final AppointmentTime appointmentTime;
    private final DurationValue duration;
    private final MoneyValue cost;
    private AppointmentStatus status;

    public AppointmentAggregate(final AppointmentId appointmentId,
                               final PatientId patientId,
                               final DoctorId doctorId,
                               final AppointmentTime appointmentTime,
                               final DurationValue duration,
                               final MoneyValue cost) {
        if (appointmentId == null) {
            throw new IllegalArgumentException("AppointmentId cannot be null");
        }
        if (patientId == null) {
            throw new IllegalArgumentException("PatientId cannot be null");
        }
        if (doctorId == null) {
            throw new IllegalArgumentException("DoctorId cannot be null");
        }
        if (appointmentTime == null) {
            throw new IllegalArgumentException("Appointment time cannot be null");
        }
        if (duration == null) {
            throw new IllegalArgumentException("Duration cannot be null");
        }
        if (cost == null) {
            throw new IllegalArgumentException("Cost cannot be null");
        }
        
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentTime = appointmentTime;
        this.duration = duration;
        this.cost = cost;
        this.status = new AppointmentStatus(AppointmentStatus.Status.SCHEDULED);
    }

    public void confirm() {
        this.status = new AppointmentStatus(AppointmentStatus.Status.CONFIRMED);
    }

    public void cancel() {
        this.status = new AppointmentStatus(AppointmentStatus.Status.CANCELLED);
    }

    public void complete() {
        this.status = new AppointmentStatus(AppointmentStatus.Status.COMPLETED);
    }

    public AppointmentId getAppointmentId() {
        return appointmentId;
    }

    public PatientId getPatientId() {
        return patientId;
    }

    public DoctorId getDoctorId() {
        return doctorId;
    }

    public AppointmentTime getAppointmentTime() {
        return appointmentTime;
    }

    public DurationValue getDuration() {
        return duration;
    }

    public MoneyValue getCost() {
        return cost;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final AppointmentAggregate that = (AppointmentAggregate) obj;
        return Objects.equals(appointmentId, that.appointmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appointmentId);
    }
} 