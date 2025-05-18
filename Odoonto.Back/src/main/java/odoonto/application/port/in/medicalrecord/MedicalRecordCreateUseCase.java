package odoonto.application.port.in.medicalrecord;

import odoonto.application.dto.request.MedicalRecordCreateDTO;
import odoonto.application.dto.response.MedicalRecordDTO;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para crear historiales médicos
 */
public interface MedicalRecordCreateUseCase {
    
    /**
     * Crea un nuevo historial médico
     * @param createDTO DTO con los datos para la creación del historial médico
     * @return Mono con el DTO del historial médico creado
     */
    Mono<MedicalRecordDTO> createMedicalRecord(MedicalRecordCreateDTO createDTO);
} 