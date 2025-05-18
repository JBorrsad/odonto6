package odoonto.application.port.in.patient;

import odoonto.application.dto.response.PatientDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Puerto de entrada (caso de uso) para consultar pacientes
 */
public interface PatientQueryUseCase {
    
    /**
     * Obtiene todos los pacientes del sistema
     * @return Flux de DTOs de pacientes
     */
    Flux<PatientDTO> getAllPatients();
    
    /**
     * Obtiene un paciente por su ID
     * @param id ID del paciente
     * @return Mono con el DTO del paciente
     */
    Mono<PatientDTO> getPatientById(String id);
    
    /**
     * Busca pacientes por criterio (nombre o apellido)
     * @param searchQuery Texto a buscar
     * @return Flux de DTOs de pacientes que coinciden con la búsqueda
     */
    Flux<PatientDTO> searchPatients(String searchQuery);
} 