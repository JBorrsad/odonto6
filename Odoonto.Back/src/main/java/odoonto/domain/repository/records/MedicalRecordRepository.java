package odoonto.domain.repository.records;

import org.jmolecules.ddd.annotation.Repository;

import odoonto.domain.model.records.aggregates.MedicalRecordAggregate;
import odoonto.domain.model.records.valueobjects.MedicalRecordId;
import odoonto.domain.model.patients.valueobjects.PatientId;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository {
    void save(MedicalRecordAggregate medicalRecord);
    Optional<MedicalRecordAggregate> findById(MedicalRecordId id);
    List<MedicalRecordAggregate> findByPatientId(PatientId patientId);
} 