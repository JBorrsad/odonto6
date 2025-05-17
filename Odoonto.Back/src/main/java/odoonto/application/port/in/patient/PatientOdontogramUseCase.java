package odoonto.application.port.in.patient;

/**
 * Puerto de entrada (caso de uso) para obtener información relacionada con el paciente
 */
public interface PatientOdontogramUseCase {
    
    /**
     * Obtiene el odontograma de un paciente
     * @param patientId ID del paciente
     * @return Odontograma del paciente
     */
    Object getPatientOdontogram(String patientId);
    
    /**
     * Obtiene el historial médico de un paciente
     * @param patientId ID del paciente
     * @return Historial médico del paciente
     */
    Object getPatientMedicalRecord(String patientId);
} 