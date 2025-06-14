package odoonto.domain.repository.staff;

import org.jmolecules.ddd.annotation.Repository;

import odoonto.domain.model.staff.aggregates.DoctorAggregate;
import odoonto.domain.model.staff.valueobjects.DoctorId;
import odoonto.domain.model.staff.valueobjects.SpecialtyValue;
import odoonto.domain.model.patients.valueobjects.EmailAddress;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository {
    void save(DoctorAggregate doctor);
    Optional<DoctorAggregate> findById(DoctorId doctorId);
    List<DoctorAggregate> findBySpecialty(SpecialtyValue specialty);
    Optional<DoctorAggregate> findByEmail(EmailAddress email);
    List<DoctorAggregate> findAll();
} 