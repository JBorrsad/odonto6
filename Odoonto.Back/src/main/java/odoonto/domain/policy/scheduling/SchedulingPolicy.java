package odoonto.domain.policy.scheduling;

import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.scheduling.valueobjects.AppointmentTime;
import odoonto.domain.model.staff.valueobjects.DoctorId;
import odoonto.domain.model.patients.valueobjects.PatientId;

public final class SchedulingPolicy {

    public void validateAppointmentScheduling(final DoctorId doctorId,
                                            final PatientId patientId,
                                            final AppointmentTime appointmentTime) {
        
        if (doctorId == null) {
            throw new DomainException("Doctor ID cannot be null");
        }
        if (patientId == null) {
            throw new DomainException("Patient ID cannot be null");
        }
        if (appointmentTime == null) {
            throw new DomainException("Appointment time cannot be null");
        }
        if (appointmentTime.isInPast()) {
            throw new DomainException("Cannot schedule appointment in the past");
        }
    }
} 