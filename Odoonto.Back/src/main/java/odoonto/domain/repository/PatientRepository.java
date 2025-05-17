package odoonto.domain.repository;

import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.model.valueobjects.PatientId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repositorio para la entidad Patient
 */
public interface PatientRepository {

    /**
     * Busca un paciente por su identificador
     * @param id Identificador único del paciente
     * @return Mono con el paciente encontrado o empty si no existe
     */
    Mono<Patient> findById(PatientId id);
    
    /**
     * Guarda un paciente en el repositorio
     * @param patient Paciente a guardar
     * @return Mono con el paciente guardado
     */
    Mono<Patient> save(Patient patient);
    
    /**
     * Elimina un paciente por su identificador
     * @param id Identificador único del paciente
     * @return Mono vacío que completa cuando la operación termina
     */
    Mono<Void> deleteById(PatientId id);
    
    /**
     * Busca todos los pacientes en el repositorio
     * @return Flux con todos los pacientes
     */
    Flux<Patient> findAll();
    
    /**
     * Busca pacientes cuyo nombre o apellido contenga el texto especificado
     * @param nombre Texto a buscar en el nombre
     * @param apellido Texto a buscar en el apellido
     * @return Flux con los pacientes que coinciden con la búsqueda
     */
    Flux<Patient> findByNombreContainingOrApellidoContaining(String nombre, String apellido);
    
    /**
     * Busca un paciente por su email
     * @param email Email a buscar
     * @return Mono con el paciente encontrado o empty si no existe
     */
    Mono<Patient> findByEmail(String email);
    
    /**
     * Busca un paciente por su teléfono
     * @param telefono Teléfono a buscar
     * @return Mono con el paciente encontrado o empty si no existe
     */
    Mono<Patient> findByTelefono(String telefono);
    
    /**
     * Método de compatibilidad para buscar por id usando String
     * @param id Identificador como String
     * @return Mono con el paciente encontrado o empty si no existe
     */
    default Mono<Patient> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Mono.empty();
        }
        return findById(PatientId.of(id));
    }
    
    /**
     * Método de compatibilidad para eliminar por id usando String
     * @param id Identificador como String
     * @return Mono vacío que completa cuando la operación termina
     */
    default Mono<Void> deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Mono.empty();
        }
        return deleteById(PatientId.of(id));
    }
}