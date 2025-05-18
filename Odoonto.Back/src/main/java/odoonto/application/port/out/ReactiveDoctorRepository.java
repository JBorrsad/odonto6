package odoonto.application.port.out;

import odoonto.domain.model.aggregates.Doctor;
import odoonto.domain.model.valueobjects.Specialty;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida reactivo para el repositorio de doctores.
 * Esta interfaz adapta el repositorio de dominio a una interfaz reactiva
 * para su uso en la capa de aplicación.
 */
public interface ReactiveDoctorRepository {
    
    /**
     * Busca un doctor por su ID
     * @param id ID del doctor
     * @return Doctor encontrado o Empty si no existe
     */
    Mono<Doctor> findById(String id);
    
    /**
     * Guarda un doctor
     * @param doctor Doctor a guardar
     * @return Doctor guardado con su ID asignado
     */
    Mono<Doctor> save(Doctor doctor);
    
    /**
     * Elimina un doctor por su ID
     * @param id ID del doctor a eliminar
     * @return Completable
     */
    Mono<Void> deleteById(String id);
    
    /**
     * Obtiene todos los doctores
     * @return Flux de doctores
     */
    Flux<Doctor> findAll();
    
    /**
     * Busca doctores por especialidad
     * @param especialidad Especialidad a buscar
     * @return Flux de doctores con la especialidad indicada
     */
    Flux<Doctor> findByEspecialidad(Specialty especialidad);
    
    /**
     * Busca doctores cuyo nombre contiene la cadena especificada
     * @param nombre Nombre o parte del nombre a buscar
     * @return Flux de doctores que coinciden con el criterio
     */
    Flux<Doctor> findByNombreCompletoContaining(String nombre);
} 