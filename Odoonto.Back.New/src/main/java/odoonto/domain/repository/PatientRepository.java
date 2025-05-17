package odoonto.domain.repository;

import odoonto.domain.model.aggregates.Patient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repositorio para el agregado Patient
 */
public interface PatientRepository {
    
    /**
     * Busca un paciente por su ID
     * @param id ID del paciente
     * @return Paciente encontrado o Empty si no existe
     */
    Mono<Patient> findById(String id);
    
    /**
     * Guarda un paciente
     * @param patient Paciente a guardar
     * @return Paciente guardado con su ID asignado
     */
    Mono<Patient> save(Patient patient);
    
    /**
     * Elimina un paciente por su ID
     * @param id ID del paciente a eliminar
     * @return Completable
     */
    Mono<Void> deleteById(String id);
    
    /**
     * Obtiene todos los pacientes
     * @return Flux de pacientes
     */
    Flux<Patient> findAll();
    
    /**
     * Busca pacientes por nombre o apellido
     * @param nombre Nombre o apellido a buscar
     * @return Flux de pacientes que coinciden con el criterio
     */
    Flux<Patient> findByNombreContainingOrApellidoContaining(String nombre, String apellido);
    
    /**
     * Busca pacientes por correo electrónico
     * @param email Correo electrónico a buscar
     * @return Paciente encontrado o Empty si no existe
     */
    Mono<Patient> findByEmail(String email);
    
    /**
     * Busca pacientes por número de teléfono
     * @param telefono Número de teléfono a buscar
     * @return Paciente encontrado o Empty si no existe
     */
    Mono<Patient> findByTelefono(String telefono);
}