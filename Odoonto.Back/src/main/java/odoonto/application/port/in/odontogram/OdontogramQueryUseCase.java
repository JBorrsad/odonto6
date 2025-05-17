package odoonto.application.port.in.odontogram;

import odoonto.application.dto.response.OdontogramDTO;
import java.util.List;
import java.util.Optional;

/**
 * Caso de uso para consultar odontogramas
 */
public interface OdontogramQueryUseCase {
    
    /**
     * Obtiene un odontograma por su ID
     * 
     * @param odontogramId ID del odontograma
     * @return Optional con el odontograma si existe
     */
    Optional<OdontogramDTO> findById(String odontogramId);
    
    /**
     * Obtiene el odontograma de un paciente específico
     * 
     * @param patientId ID del paciente
     * @return Optional con el odontograma si existe
     */
    Optional<OdontogramDTO> findByPatientId(String patientId);
    
    /**
     * Obtiene todos los odontogramas
     * 
     * @return Lista de odontogramas
     */
    List<OdontogramDTO> findAll();
    
    /**
     * Verifica si un odontograma existe
     * 
     * @param odontogramId ID del odontograma
     * @return true si existe
     */
    boolean existsById(String odontogramId);
    
    /**
     * Verifica si un paciente tiene odontograma
     * 
     * @param patientId ID del paciente
     * @return true si tiene odontograma
     */
    boolean existsByPatientId(String patientId);
} 