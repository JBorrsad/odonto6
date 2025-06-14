package odoonto.domain.repository.scheduling;

import org.jmolecules.ddd.annotation.Repository;

import odoonto.domain.model.scheduling.aggregates.AppointmentAggregate;
import odoonto.domain.model.scheduling.valueobjects.AppointmentId;
import odoonto.domain.model.scheduling.valueobjects.AppointmentTime;
import odoonto.domain.model.patients.valueobjects.PatientId;
import odoonto.domain.model.staff.valueobjects.DoctorId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository {
    void save(AppointmentAggregate appointment);
    Optional<AppointmentAggregate> findById(AppointmentId appointmentId);
    List<AppointmentAggregate> findByPatientId(PatientId patientId);
    List<AppointmentAggregate> findByDoctorId(DoctorId doctorId);
    List<AppointmentAggregate> findByDoctorIdAndDate(DoctorId doctorId, LocalDate date);
    List<AppointmentAggregate> findByAppointmentTime(AppointmentTime appointmentTime);
} 