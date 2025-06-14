package odoonto.domain.events.scheduling;

import odoonto.domain.model.scheduling.valueobjects.AppointmentId;
import odoonto.domain.model.scheduling.valueobjects.AppointmentTime;
import odoonto.domain.model.patients.valueobjects.PatientId;
import odoonto.domain.model.staff.valueobjects.DoctorId;
import odoonto.domain.model.shared.valueobjects.EventId;
import odoonto.domain.model.shared.valueobjects.TimestampValue;

import java.util.Objects;

public final class AppointmentScheduledEvent {
    
    private final EventId eventId;
    private final AppointmentId appointmentId;
    private final PatientId patientId;
    private final DoctorId doctorId;
    private final AppointmentTime appointmentTime;
    private final String treatmentType;
    private final String notes;
    private final TimestampValue occurredAt;

    private AppointmentScheduledEvent(final EventId eventId,
                                     final AppointmentId appointmentId,
                                     final PatientId patientId,
                                     final DoctorId doctorId,
                                     final AppointmentTime appointmentTime,
                                     final String treatmentType,
                                     final String notes,
                                     final TimestampValue occurredAt) {
        validateParameters(eventId, appointmentId, patientId, doctorId, appointmentTime, occurredAt);
        this.eventId = eventId;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentTime = appointmentTime;
        this.treatmentType = treatmentType;
        this.notes = notes;
        this.occurredAt = occurredAt;
    }

    public static AppointmentScheduledEvent create(final AppointmentId appointmentId,
                                                  final PatientId patientId,
                                                  final DoctorId doctorId,
                                                  final AppointmentTime appointmentTime,
                                                  final String treatmentType,
                                                  final String notes) {
        return new AppointmentScheduledEvent(
            EventId.generate(),
            appointmentId,
            patientId,
            doctorId,
            appointmentTime,
            treatmentType,
            notes,
            TimestampValue.now()
        );
    }

    public static AppointmentScheduledEvent reconstruct(final EventId eventId,
                                                       final AppointmentId appointmentId,
                                                       final PatientId patientId,
                                                       final DoctorId doctorId,
                                                       final AppointmentTime appointmentTime,
                                                       final String treatmentType,
                                                       final String notes,
                                                       final TimestampValue occurredAt) {
        return new AppointmentScheduledEvent(eventId, appointmentId, patientId, doctorId,
                                           appointmentTime, treatmentType, notes, occurredAt);
    }

    private static void validateParameters(final EventId eventId,
                                          final AppointmentId appointmentId,
                                          final PatientId patientId,
                                          final DoctorId doctorId,
                                          final AppointmentTime appointmentTime,
                                          final TimestampValue occurredAt) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        if (appointmentId == null) {
            throw new IllegalArgumentException("Appointment ID cannot be null");
        }
        if (patientId == null) {
            throw new IllegalArgumentException("Patient ID cannot be null");
        }
        if (doctorId == null) {
            throw new IllegalArgumentException("Doctor ID cannot be null");
        }
        if (appointmentTime == null) {
            throw new IllegalArgumentException("Appointment time cannot be null");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("Occurred timestamp cannot be null");
        }
    }

    public EventId getEventId() {
        return eventId;
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

    public String getTreatmentType() {
        return treatmentType;
    }

    public String getNotes() {
        return notes;
    }

    public TimestampValue getOccurredAt() {
        return occurredAt;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final AppointmentScheduledEvent that = (AppointmentScheduledEvent) other;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "AppointmentScheduledEvent{" +
                "eventId=" + eventId +
                ", appointmentId=" + appointmentId +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", appointmentTime=" + appointmentTime +
                ", occurredAt=" + occurredAt +
                '}';
    }
} 