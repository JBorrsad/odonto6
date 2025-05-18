package odoonto.application.port.in.medicalrecord;

import odoonto.application.dto.response.MedicalRecordDTO;
import java.util.List;
import java.util.Optional;

/**
 * Caso de uso para consultar historiales médicos
 */
public interface MedicalRecordQueryUseCase {
    Optional<MedicalRecordDTO> findById(String medicalRecordId);
    Optional<MedicalRecordDTO> findByPatientId(String patientId);
    List<MedicalRecordDTO> findAll();
    boolean existsById(String medicalRecordId);
    boolean existsByPatientId(String patientId);
} 