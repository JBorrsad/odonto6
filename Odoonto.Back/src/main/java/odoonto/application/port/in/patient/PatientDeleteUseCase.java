package odoonto.application.port.in.patient;

import reactor.core.publisher.Mono;

/**
 * Puerto de entrada (caso de uso) para eliminar un paciente
 */
public interface PatientDeleteUseCase {
    
    /**
     * Elimina un paciente por su ID
     * @param id ID del paciente a eliminar
     * @return Mono que completa cuando se elimina el paciente
     */
    Mono<Void> deletePatient(String id);
} 