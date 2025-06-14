package odoonto.domain.repository.records;

import odoonto.domain.model.records.aggregates.MedicalRecordAggregate;
import odoonto.domain.model.records.valueobjects.MedicalRecordId;
import odoonto.domain.model.records.valueobjects.ToothNumber;
import odoonto.domain.model.records.valueobjects.TreatmentId;
import odoonto.domain.model.records.entities.MedicalEntryEntity;
import odoonto.domain.model.patients.valueobjects.PatientId;
import odoonto.domain.model.staff.valueobjects.DoctorId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository {
    
    void save(MedicalRecordAggregate medicalRecord);
    
    Optional<MedicalRecordAggregate> findById(MedicalRecordId medicalRecordId);
    
    Optional<MedicalRecordAggregate> findByPatientId(PatientId patientId);
    
    List<MedicalRecordAggregate> findByDoctorId(DoctorId doctorId);
    
    List<MedicalEntryEntity> findTreatmentHistory(PatientId patientId, ToothNumber toothNumber);
    
    List<MedicalEntryEntity> findRecentTreatments(PatientId patientId, int days);
    
    List<MedicalEntryEntity> findTreatmentsByType(PatientId patientId, String treatmentType);
    
    List<MedicalEntryEntity> findTreatmentsByDateRange(PatientId patientId, 
                                                      LocalDate startDate, 
                                                      LocalDate endDate);
    
    List<MedicalEntryEntity> findClinicalNotes(PatientId patientId);
    
    List<MedicalEntryEntity> findLesionHistory(PatientId patientId, ToothNumber toothNumber);
    
    List<MedicalEntryEntity> findActiveLesions(PatientId patientId);
    
    List<MedicalEntryEntity> findTreatmentsByDoctor(PatientId patientId, DoctorId doctorId);
    
    List<ToothNumber> findTeethWithActiveTreatments(PatientId patientId);
    
    List<ToothNumber> findTeethWithLesions(PatientId patientId);
    
    List<ToothNumber> findHealthyTeeth(PatientId patientId);
    
    Optional<MedicalEntryEntity> findLastTreatmentForTooth(PatientId patientId, 
                                                          ToothNumber toothNumber);
    
    Optional<MedicalEntryEntity> findLastClinicalNote(PatientId patientId);
    
    boolean hasActiveTreatmentForTooth(PatientId patientId, ToothNumber toothNumber);
    
    boolean hasLesionForTooth(PatientId patientId, ToothNumber toothNumber);
    
    boolean hasTreatmentHistory(PatientId patientId);
    
    long countTreatmentsByPatient(PatientId patientId);
    
    long countLesionsByPatient(PatientId patientId);
    
    long countClinicalNotesByPatient(PatientId patientId);
    
    long countTreatmentsByType(String treatmentType);
    
    void delete(MedicalRecordId medicalRecordId);
} 