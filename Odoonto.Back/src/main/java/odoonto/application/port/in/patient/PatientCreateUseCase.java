package odoonto.application.port.in.patient;

import odoonto.application.dto.request.PatientCreateDTO;
import odoonto.application.dto.response.PatientDTO;
import reactor.core.publisher.Mono;

/**
 * Puerto de entrada (caso de uso) para crear un nuevo paciente
 */
public interface PatientCreateUseCase {
    
    /**
     * Crea un nuevo paciente en el sistema
     * @param patientDTO Datos del paciente a crear
     * @return Mono con el DTO del paciente creado con su ID asignado
     */
    Mono<PatientDTO> createPatient(PatientCreateDTO patientDTO);
} 