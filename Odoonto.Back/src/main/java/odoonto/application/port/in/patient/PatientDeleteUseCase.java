package odoonto.application.port.in.patient;

/**
 * Puerto de entrada (caso de uso) para eliminar un paciente
 */
public interface PatientDeleteUseCase {
    
    /**
     * Elimina un paciente por su ID
     * @param id ID del paciente a eliminar
     */
    void deletePatient(String id);
} 