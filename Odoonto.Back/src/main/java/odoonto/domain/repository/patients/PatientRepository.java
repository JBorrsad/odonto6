package odoonto.domain.repository.patients;

import org.jmolecules.ddd.annotation.Repository;
import odoonto.domain.model.patients.aggregates.PatientAggregate;
import odoonto.domain.model.patients.valueobjects.PatientId;
import odoonto.domain.model.patients.valueobjects.EmailAddress;

import java.util.Optional;

@Repository
public interface PatientRepository {
    
    void save(PatientAggregate patient);
    
    Optional<PatientAggregate> findById(PatientId id);
    
    Optional<PatientAggregate> findByEmail(EmailAddress email);
    
    void delete(PatientId id);
} 