package odoonto.domain.repository.patients;

import odoonto.domain.model.patients.aggregates.PatientAggregate;
import odoonto.domain.model.patients.valueobjects.PatientId;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {
    
    void save(PatientAggregate patient);
    
    Optional<PatientAggregate> findById(PatientId patientId);
    
    List<PatientAggregate> findAll();
    
    void delete(PatientId patientId);
} 