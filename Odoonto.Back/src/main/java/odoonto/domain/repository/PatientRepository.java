package odoonto.domain.repository;

import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.model.valueobjects.PatientId;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Patient.
 * Define operaciones puras de dominio sin dependencias externas.
 */
public interface PatientRepository {

    /**
     * Busca un paciente por su identificador
     * @param id Identificador único del paciente
     * @return Optional con el paciente encontrado o empty si no existe
     */
    Optional<Patient> findById(PatientId id);
    
    /**
     * Guarda un paciente en el repositorio
     * @param patient Paciente a guardar
     * @return El paciente guardado
     */
    Patient save(Patient patient);
    
    /**
     * Elimina un paciente por su identificador
     * @param id Identificador único del paciente
     */
    void deleteById(PatientId id);
    
    /**
     * Busca todos los pacientes en el repositorio
     * @return Lista con todos los pacientes
     */
    List<Patient> findAll();
    
    /**
     * Busca pacientes cuyo nombre o apellido contenga el texto especificado
     * @param nombre Texto a buscar en el nombre
     * @param apellido Texto a buscar en el apellido
     * @return Lista con los pacientes que coinciden con la búsqueda
     */
    List<Patient> findByNombreContainingOrApellidoContaining(String nombre, String apellido);
    
    /**
     * Busca un paciente por su email
     * @param email Email a buscar
     * @return Optional con el paciente encontrado o empty si no existe
     */
    Optional<Patient> findByEmail(String email);
    
    /**
     * Busca un paciente por su teléfono
     * @param telefono Teléfono a buscar
     * @return Optional con el paciente encontrado o empty si no existe
     */
    Optional<Patient> findByTelefono(String telefono);
    
    /**
     * Método de compatibilidad para buscar por id usando String
     * @param id Identificador como String
     * @return Optional con el paciente encontrado o empty si no existe
     */
    default Optional<Patient> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        return findById(PatientId.of(id));
    }
    
    /**
     * Método de compatibilidad para eliminar por id usando String
     * @param id Identificador como String
     */
    default void deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return;
        }
        deleteById(PatientId.of(id));
    }
}