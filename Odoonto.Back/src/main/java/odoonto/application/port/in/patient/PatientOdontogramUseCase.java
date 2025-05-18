package odoonto.application.port.in.patient;

import odoonto.application.dto.response.MedicalRecordDTO;
import odoonto.application.dto.response.OdontogramDTO;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.valueobjects.MedicalRecordId;
import reactor.core.publisher.Mono;

/**
 * Puerto de entrada (caso de uso) para obtener información relacionada con el paciente
 */
public interface PatientOdontogramUseCase {
    
    /**
     * Obtiene el odontograma de un paciente
     * @param patientId ID del paciente
     * @return Mono con el odontograma del paciente
     */
    Mono<Odontogram> getPatientOdontogram(String patientId);
    
    /**
     * Obtiene el odontograma de un paciente como DTO
     * @param patientId ID del paciente
     * @return Mono con el DTO del odontograma del paciente
     */
    Mono<OdontogramDTO> getPatientOdontogramDTO(String patientId);
    
    /**
     * Obtiene el historial médico de un paciente
     * @param patientId ID del paciente
     * @return Mono con el ID del historial médico del paciente
     */
    Mono<MedicalRecordId> getPatientMedicalRecord(String patientId);
    
    /**
     * Obtiene el historial médico de un paciente como DTO
     * @param patientId ID del paciente
     * @return Mono con el DTO del historial médico del paciente
     */
    Mono<MedicalRecordDTO> getPatientMedicalRecordDTO(String patientId);
} 