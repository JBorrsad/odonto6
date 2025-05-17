package odoonto.application.port.in.patient;

import odoonto.application.dto.response.PatientDTO;

import java.util.List;

/**
 * Puerto de entrada (caso de uso) para consultar pacientes
 */
public interface PatientQueryUseCase {
    
    /**
     * Obtiene todos los pacientes del sistema
     * @return Lista de DTOs de pacientes
     */
    List<PatientDTO> getAllPatients();
    
    /**
     * Obtiene un paciente por su ID
     * @param id ID del paciente
     * @return DTO del paciente
     */
    PatientDTO getPatientById(String id);
    
    /**
     * Busca pacientes por criterio (nombre o apellido)
     * @param searchQuery Texto a buscar
     * @return Lista de DTOs de pacientes que coinciden con la búsqueda
     */
    List<PatientDTO> searchPatients(String searchQuery);
} 