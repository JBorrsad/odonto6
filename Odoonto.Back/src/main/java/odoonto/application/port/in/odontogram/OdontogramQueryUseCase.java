package odoonto.application.port.in.odontogram;

import odoonto.application.dto.response.OdontogramDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para consultar odontogramas
 */
public interface OdontogramQueryUseCase {
    
    /**
     * Obtiene un odontograma por su ID
     * 
     * @param odontogramId ID del odontograma
     * @return Mono con el odontograma si existe
     */
    Mono<OdontogramDTO> findById(String odontogramId);
    
    /**
     * Obtiene el odontograma de un paciente específico
     * 
     * @param patientId ID del paciente
     * @return Mono con el odontograma si existe
     */
    Mono<OdontogramDTO> findByPatientId(String patientId);
    
    /**
     * Obtiene todos los odontogramas
     * 
     * @return Flux de odontogramas
     */
    Flux<OdontogramDTO> findAll();
    
    /**
     * Verifica si un odontograma existe
     * 
     * @param odontogramId ID del odontograma
     * @return Mono con true si existe
     */
    Mono<Boolean> existsById(String odontogramId);
    
    /**
     * Verifica si un paciente tiene odontograma
     * 
     * @param patientId ID del paciente
     * @return Mono con true si tiene odontograma
     */
    Mono<Boolean> existsByPatientId(String patientId);
} 