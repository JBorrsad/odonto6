package odoonto.domain.repository.scheduling;

import odoonto.domain.model.scheduling.aggregates.AppointmentAggregate;
import odoonto.domain.model.scheduling.valueobjects.AppointmentId;
import odoonto.domain.model.patients.valueobjects.PatientId;
import odoonto.domain.model.staff.valueobjects.DoctorId;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {
    
    void save(AppointmentAggregate appointment);
    
    Optional<AppointmentAggregate> findById(AppointmentId appointmentId);
    
    List<AppointmentAggregate> findByPatientId(PatientId patientId);
    
    List<AppointmentAggregate> findByDoctorId(DoctorId doctorId);
    
    List<AppointmentAggregate> findAll();
    
    void delete(AppointmentId appointmentId);
} 