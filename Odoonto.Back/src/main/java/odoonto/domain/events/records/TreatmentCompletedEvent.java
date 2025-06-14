package odoonto.domain.events.records;

import odoonto.domain.model.scheduling.valueobjects.AppointmentId;
import odoonto.domain.model.patients.valueobjects.PatientId;
import odoonto.domain.model.staff.valueobjects.DoctorId;
import odoonto.domain.model.records.valueobjects.ToothNumber;
import odoonto.domain.model.records.valueobjects.TreatmentId;
import odoonto.domain.model.catalog.valueobjects.TreatmentType;
import odoonto.domain.model.shared.valueobjects.EventId;
import odoonto.domain.model.shared.valueobjects.TimestampValue;
import odoonto.domain.model.shared.valueobjects.MoneyValue;

import java.util.Objects;
import java.util.List;

public final class TreatmentCompletedEvent {
    
    private final EventId eventId;
    private final AppointmentId appointmentId;
    private final PatientId patientId;
    private final DoctorId doctorId;
    private final TreatmentId treatmentId;
    private final TreatmentType treatmentType;
    private final String treatmentName;
    private final List<ToothNumber> affectedTeeth;
    private final MoneyValue cost;
    private final String notes;
    private final String complications;
    private final TimestampValue completedAt;

    private TreatmentCompletedEvent(final EventId eventId,
                                   final AppointmentId appointmentId,
                                   final PatientId patientId,
                                   final DoctorId doctorId,
                                   final TreatmentId treatmentId,
                                   final TreatmentType treatmentType,
                                   final String treatmentName,
                                   final List<ToothNumber> affectedTeeth,
                                   final MoneyValue cost,
                                   final String notes,
                                   final String complications,
                                   final TimestampValue completedAt) {
        validateParameters(eventId, appointmentId, patientId, doctorId, treatmentId, 
                          treatmentType, treatmentName, completedAt);
        this.eventId = eventId;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.treatmentId = treatmentId;
        this.treatmentType = treatmentType;
        this.treatmentName = treatmentName;
        this.affectedTeeth = affectedTeeth != null ? List.copyOf(affectedTeeth) : List.of();
        this.cost = cost != null ? cost : MoneyValue.zeroPesos();
        this.notes = notes != null ? notes : "";
        this.complications = complications != null ? complications : "";
        this.completedAt = completedAt;
    }

    public static TreatmentCompletedEvent create(final AppointmentId appointmentId,
                                                final PatientId patientId,
                                                final DoctorId doctorId,
                                                final TreatmentId treatmentId,
                                                final TreatmentType treatmentType,
                                                final String treatmentName,
                                                final List<ToothNumber> affectedTeeth,
                                                final MoneyValue cost,
                                                final String notes,
                                                final String complications) {
        return new TreatmentCompletedEvent(
            EventId.generate(),
            appointmentId,
            patientId,
            doctorId,
            treatmentId,
            treatmentType,
            treatmentName,
            affectedTeeth,
            cost,
            notes,
            complications,
            TimestampValue.now()
        );
    }

    public static TreatmentCompletedEvent reconstruct(final EventId eventId,
                                                     final AppointmentId appointmentId,
                                                     final PatientId patientId,
                                                     final DoctorId doctorId,
                                                     final TreatmentId treatmentId,
                                                     final TreatmentType treatmentType,
                                                     final String treatmentName,
                                                     final List<ToothNumber> affectedTeeth,
                                                     final MoneyValue cost,
                                                     final String notes,
                                                     final String complications,
                                                     final TimestampValue completedAt) {
        return new TreatmentCompletedEvent(eventId, appointmentId, patientId, doctorId,
                                          treatmentId, treatmentType, treatmentName,
                                          affectedTeeth, cost, notes, complications, completedAt);
    }

    public boolean hasComplications() {
        return complications != null && !complications.trim().isEmpty();
    }

    public boolean isExpensiveTreatment() {
        return cost != null && cost.getAmount().doubleValue() > 500000.0;
    }

    public boolean affectsMultipleTeeth() {
        return affectedTeeth.size() > 1;
    }

    public boolean requiresFollowUp() {
        return hasComplications() || treatmentType.requiresSpecialist() || isExpensiveTreatment();
    }

    private static void validateParameters(final EventId eventId,
                                          final AppointmentId appointmentId,
                                          final PatientId patientId,
                                          final DoctorId doctorId,
                                          final TreatmentId treatmentId,
                                          final TreatmentType treatmentType,
                                          final String treatmentName,
                                          final TimestampValue completedAt) {
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
        if (treatmentId == null) {
            throw new IllegalArgumentException("Treatment ID cannot be null");
        }
        if (treatmentType == null) {
            throw new IllegalArgumentException("Treatment type cannot be null");
        }
        if (treatmentName == null || treatmentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment name cannot be null or empty");
        }
        if (completedAt == null) {
            throw new IllegalArgumentException("Completed timestamp cannot be null");
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

    public TreatmentId getTreatmentId() {
        return treatmentId;
    }

    public TreatmentType getTreatmentType() {
        return treatmentType;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public List<ToothNumber> getAffectedTeeth() {
        return affectedTeeth;
    }

    public MoneyValue getCost() {
        return cost;
    }

    public String getNotes() {
        return notes;
    }

    public String getComplications() {
        return complications;
    }

    public TimestampValue getCompletedAt() {
        return completedAt;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final TreatmentCompletedEvent that = (TreatmentCompletedEvent) other;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "TreatmentCompletedEvent{" +
                "eventId=" + eventId +
                ", appointmentId=" + appointmentId +
                ", patientId=" + patientId +
                ", treatmentName='" + treatmentName + '\'' +
                ", affectedTeeth=" + affectedTeeth.size() +
                ", cost=" + cost +
                ", completedAt=" + completedAt +
                '}';
    }
} 