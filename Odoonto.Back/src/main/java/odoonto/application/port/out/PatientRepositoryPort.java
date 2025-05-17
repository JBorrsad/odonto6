package odoonto.application.port.out;

import odoonto.domain.model.aggregates.Patient;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para el repositorio de pacientes
 * Abstrae el acceso a datos para la entidad Patient
 */
public interface PatientRepositoryPort {
    
    /**
     * Guarda un paciente en el repositorio
     * @param patient Paciente a guardar
     * @return Paciente con posibles cambios (como ID asignado)
     */
    Patient save(Patient patient);
    
    /**
     * Busca un paciente por su ID
     * @param id ID del paciente
     * @return Optional con el paciente si existe
     */
    Optional<Patient> findById(String id);
    
    /**
     * Obtiene todos los pacientes
     * @return Lista de pacientes
     */
    List<Patient> findAll();
    
    /**
     * Elimina un paciente por su ID
     * @param id ID del paciente a eliminar
     */
    void deleteById(String id);
    
    /**
     * Busca pacientes por nombre o apellido
     * @param searchTerm Término de búsqueda
     * @return Lista de pacientes que coinciden
     */
    List<Patient> findByNameOrLastName(String searchTerm);
} 