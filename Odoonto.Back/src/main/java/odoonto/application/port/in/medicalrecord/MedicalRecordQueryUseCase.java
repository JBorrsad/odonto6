package odoonto.application.port.in.medicalrecord;

import odoonto.application.dto.response.MedicalRecordDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para consultar historiales médicos
 */
public interface MedicalRecordQueryUseCase {
    /**
     * Busca un historial médico por su ID
     * @param medicalRecordId ID del historial médico
     * @return Mono con el DTO del historial médico si existe
     */
    Mono<MedicalRecordDTO> findById(String medicalRecordId);
    
    /**
     * Busca un historial médico por el ID del paciente
     * @param patientId ID del paciente
     * @return Mono con el DTO del historial médico si existe
     */
    Mono<MedicalRecordDTO> findByPatientId(String patientId);
    
    /**
     * Obtiene todos los historiales médicos
     * @return Flux de DTOs de historiales médicos
     */
    Flux<MedicalRecordDTO> findAll();
    
    /**
     * Verifica si existe un historial médico con el ID dado
     * @param medicalRecordId ID del historial médico
     * @return Mono con true si existe, false si no
     */
    Mono<Boolean> existsById(String medicalRecordId);
    
    /**
     * Verifica si existe un historial médico para el paciente dado
     * @param patientId ID del paciente
     * @return Mono con true si existe, false si no
     */
    Mono<Boolean> existsByPatientId(String patientId);
} 