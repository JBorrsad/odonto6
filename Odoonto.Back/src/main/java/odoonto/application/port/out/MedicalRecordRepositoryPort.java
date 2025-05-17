package odoonto.application.port.out;

import odoonto.domain.model.aggregates.MedicalRecord;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para el repositorio de historiales médicos
 * Abstrae el acceso a datos para la entidad MedicalRecord
 */
public interface MedicalRecordRepositoryPort {
    
    /**
     * Guarda un historial médico en el repositorio
     * @param medicalRecord Historial médico a guardar
     * @return Historial médico con posibles cambios (como ID asignado)
     */
    MedicalRecord save(MedicalRecord medicalRecord);
    
    /**
     * Busca un historial médico por su ID
     * @param id ID del historial médico
     * @return Optional con el historial médico si existe
     */
    Optional<MedicalRecord> findById(String id);
    
    /**
     * Obtiene el historial médico de un paciente
     * @param patientId ID del paciente
     * @return Optional con el historial médico si existe
     */
    Optional<MedicalRecord> findByPatientId(String patientId);
    
    /**
     * Obtiene todos los historiales médicos
     * @return Lista de historiales médicos
     */
    List<MedicalRecord> findAll();
    
    /**
     * Elimina un historial médico por su ID
     * @param id ID del historial médico a eliminar
     */
    void deleteById(String id);
} 