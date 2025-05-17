package odoonto.application.port.in.patient;

import odoonto.application.dto.request.PatientCreateDTO;
import odoonto.application.dto.response.PatientDTO;

/**
 * Puerto de entrada (caso de uso) para actualizar un paciente
 */
public interface PatientUpdateUseCase {
    
    /**
     * Actualiza un paciente por su ID
     * @param id ID del paciente a actualizar
     * @param patientDTO Datos actualizados del paciente
     * @return DTO del paciente actualizado
     */
    PatientDTO updatePatient(String id, PatientCreateDTO patientDTO);
} 