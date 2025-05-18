package odoonto.application.port.out;

import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.model.valueobjects.PatientId;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida reactivo para el repositorio de pacientes.
 * Define las operaciones reactivas para acceder y manipular pacientes
 * en la capa de infraestructura.
 */
public interface ReactivePatientRepository {

    /**
     * Busca todos los pacientes
     * @return Flux con todos los pacientes
     */
    Flux<Patient> findAll();
    
    /**
     * Busca un paciente por su identificador
     * @param id Identificador único del paciente
     * @return Mono con el paciente encontrado o empty si no existe
     */
    Mono<Patient> findById(String id);
    
    /**
     * Busca un paciente por su identificador de valor
     * @param patientId Identificador de paciente (objeto valor)
     * @return Mono con el paciente encontrado o empty si no existe
     */
    Mono<Patient> findById(PatientId patientId);
    
    /**
     * Guarda un paciente en el repositorio
     * @param patient Paciente a guardar
     * @return Mono con el paciente guardado
     */
    Mono<Patient> save(Patient patient);
    
    /**
     * Elimina un paciente por su identificador
     * @param id Identificador único del paciente
     * @return Mono que completa cuando la operación termina
     */
    Mono<Void> deleteById(String id);
    
    /**
     * Busca pacientes por su nombre
     * @param name Nombre del paciente
     * @return Flux con los pacientes que coinciden con el nombre
     */
    Flux<Patient> findByNameContaining(String name);
    
    /**
     * Busca pacientes por su dirección
     * @param address Dirección del paciente
     * @return Flux con los pacientes que coinciden con la dirección
     */
    Flux<Patient> findByAddressContaining(String address);
    
    /**
     * Busca pacientes por su número de teléfono
     * @param phone Número de teléfono del paciente
     * @return Flux con los pacientes que coinciden con el teléfono
     */
    Flux<Patient> findByPhoneContaining(String phone);
    
    /**
     * Comprueba si existe un paciente con el identificador proporcionado
     * @param id Identificador único del paciente
     * @return Mono con true si existe, false si no
     */
    Mono<Boolean> existsById(String id);
} 