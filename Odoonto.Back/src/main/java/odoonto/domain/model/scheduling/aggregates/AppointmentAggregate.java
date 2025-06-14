package odoonto.domain.model.scheduling.aggregates;

import org.jmolecules.ddd.annotation.AggregateRoot;

import odoonto.domain.model.scheduling.valueobjects.AppointmentId;
import odoonto.domain.model.scheduling.valueobjects.AppointmentTime;
import odoonto.domain.model.scheduling.valueobjects.AppointmentStatus;
import odoonto.domain.model.patients.valueobjects.PatientId;
import odoonto.domain.model.staff.valueobjects.DoctorId;
import odoonto.domain.model.records.valueobjects.ToothNumber;
import odoonto.domain.model.records.valueobjects.TreatmentId;
import odoonto.domain.events.scheduling.AppointmentScheduledEvent;
import odoonto.domain.events.records.TreatmentCompletedEvent;
import odoonto.domain.model.catalog.valueobjects.TreatmentType;
import odoonto.domain.model.shared.valueobjects.MoneyValue;
import odoonto.domain.exceptions.DomainException;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

@AggregateRoot
public class AppointmentAggregate {
    
    private final AppointmentId appointmentId;
    private final PatientId patientId;
    private final DoctorId doctorId;
    private final List<ToothNumber> affectedTeeth;
    private final TreatmentId treatmentId;
    private AppointmentTime appointmentTime;
    private AppointmentStatus status;

    public AppointmentAggregate(final PatientId patientId,
                               final DoctorId doctorId,
                               final List<ToothNumber> affectedTeeth,
                               final TreatmentId treatmentId,
                               final AppointmentTime appointmentTime) {
        validateConstructorParameters(patientId, doctorId, treatmentId, appointmentTime);
        
        this.appointmentId = AppointmentId.generate();
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.affectedTeeth = new ArrayList<>(affectedTeeth != null ? affectedTeeth : new ArrayList<>());
        this.treatmentId = treatmentId;
        this.appointmentTime = appointmentTime;
        this.status = AppointmentStatus.SCHEDULED;
    }

    private AppointmentAggregate(final AppointmentId appointmentId,
                                final PatientId patientId,
                                final DoctorId doctorId,
                                final List<ToothNumber> affectedTeeth,
                                final TreatmentId treatmentId,
                                final AppointmentTime appointmentTime,
                                final AppointmentStatus status) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.affectedTeeth = new ArrayList<>(affectedTeeth);
        this.treatmentId = treatmentId;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public static AppointmentAggregate reconstituteFromPersistence(
            final AppointmentId appointmentId,
            final PatientId patientId,
            final DoctorId doctorId,
            final List<ToothNumber> affectedTeeth,
            final TreatmentId treatmentId,
            final AppointmentTime appointmentTime,
            final AppointmentStatus status) {
        
        validateReconstitutionParameters(appointmentId, patientId, doctorId, treatmentId, appointmentTime, status);
        
        return new AppointmentAggregate(appointmentId, patientId, doctorId, 
                                       affectedTeeth, treatmentId, appointmentTime, status);
    }

    public void reschedule(final AppointmentTime newAppointmentTime) {
        if (newAppointmentTime == null) {
            throw new DomainException("New appointment time cannot be null");
        }
        
        validateAppointmentCanBeModified();
        this.appointmentTime = newAppointmentTime;
        this.status = AppointmentStatus.RESCHEDULED;
    }

    public void cancel() {
        validateAppointmentCanBeModified();
        this.status = AppointmentStatus.CANCELLED;
    }

    public TreatmentCompletedEvent finish(final String treatmentName,
                                          final TreatmentType treatmentType,
                                          final MoneyValue cost,
                                          final String notes,
                                          final String complications) {
        if (!status.equals(AppointmentStatus.SCHEDULED) && !status.equals(AppointmentStatus.RESCHEDULED)) {
            throw new DomainException("Cannot finish appointment with status: " + status);
        }
        
        this.status = AppointmentStatus.COMPLETED;
        
        return TreatmentCompletedEvent.create(
            appointmentId,
            patientId,
            doctorId,
            treatmentId,
            treatmentType,
            treatmentName,
            affectedTeeth,
            cost,
            notes,
            complications
        );
    }

    public AppointmentScheduledEvent generateScheduledEvent(final String treatmentType, final String notes) {
        return AppointmentScheduledEvent.create(
            appointmentId,
            patientId,
            doctorId,
            appointmentTime,
            treatmentType,
            notes
        );
    }

    public boolean isActive() {
        return status.equals(AppointmentStatus.SCHEDULED) || status.equals(AppointmentStatus.RESCHEDULED);
    }

    public boolean isCompleted() {
        return status.equals(AppointmentStatus.COMPLETED);
    }

    public boolean isCancelled() {
        return status.equals(AppointmentStatus.CANCELLED);
    }

    private void validateAppointmentCanBeModified() {
        if (status.equals(AppointmentStatus.COMPLETED)) {
            throw new DomainException("Cannot modify completed appointment");
        }
        if (status.equals(AppointmentStatus.CANCELLED)) {
            throw new DomainException("Cannot modify cancelled appointment");
        }
    }

    private static void validateConstructorParameters(final PatientId patientId,
                                                     final DoctorId doctorId,
                                                     final TreatmentId treatmentId,
                                                     final AppointmentTime appointmentTime) {
        if (patientId == null) {
            throw new DomainException("Patient ID cannot be null");
        }
        if (doctorId == null) {
            throw new DomainException("Doctor ID cannot be null");
        }
        if (treatmentId == null) {
            throw new DomainException("Treatment ID cannot be null");
        }
        if (appointmentTime == null) {
            throw new DomainException("Appointment time cannot be null");
        }
    }

    private static void validateReconstitutionParameters(final AppointmentId appointmentId,
                                                        final PatientId patientId,
                                                        final DoctorId doctorId,
                                                        final TreatmentId treatmentId,
                                                        final AppointmentTime appointmentTime,
                                                        final AppointmentStatus status) {
        if (appointmentId == null) {
            throw new DomainException("Appointment ID cannot be null");
        }
        if (status == null) {
            throw new DomainException("Appointment status cannot be null");
        }
        validateConstructorParameters(patientId, doctorId, treatmentId, appointmentTime);
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

    public List<ToothNumber> getAffectedTeeth() {
        return Collections.unmodifiableList(affectedTeeth);
    }

    public TreatmentId getTreatmentId() {
        return treatmentId;
    }

    public AppointmentTime getAppointmentTime() {
        return appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final AppointmentAggregate that = (AppointmentAggregate) other;
        return Objects.equals(appointmentId, that.appointmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appointmentId);
    }
} 