package odoonto.domain.repository;

import odoonto.domain.model.aggregates.Appointment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repositorio para el agregado Appointment
 */
public interface AppointmentRepository {
    
    /**
     * Busca una cita por su ID
     * @param id ID de la cita
     * @return Cita encontrada o Empty si no existe
     */
    Mono<Appointment> findById(String id);
    
    /**
     * Guarda una cita
     * @param appointment Cita a guardar
     * @return Cita guardada con su ID asignado
     */
    Mono<Appointment> save(Appointment appointment);
    
    /**
     * Elimina una cita por su ID
     * @param id ID de la cita a eliminar
     * @return Completable
     */
    Mono<Void> deleteById(String id);
    
    /**
     * Obtiene todas las citas
     * @return Flux de citas
     */
    Flux<Appointment> findAll();
    
    /**
     * Busca citas por ID del paciente
     * @param patientId ID del paciente
     * @return Flux de citas del paciente
     */
    Flux<Appointment> findByPatientId(String patientId);
    
    /**
     * Busca citas por ID del doctor
     * @param doctorId ID del doctor
     * @return Flux de citas del doctor
     */
    Flux<Appointment> findByDoctorId(String doctorId);
    
    /**
     * Busca citas en un rango de fechas para un doctor específico
     * @param doctorId ID del doctor
     * @param from Fecha de inicio (en formato ISO-8601)
     * @param to Fecha de fin (en formato ISO-8601)
     * @return Flux de citas que cumplen el criterio
     */
    Flux<Appointment> findByDoctorIdAndStartGreaterThanEqualAndStartLessThanEqual(
            String doctorId, String from, String to);
    
    /**
     * Busca citas en un rango de fechas para un paciente específico
     * @param patientId ID del paciente
     * @param from Fecha de inicio (en formato ISO-8601)
     * @param to Fecha de fin (en formato ISO-8601)
     * @return Flux de citas que cumplen el criterio
     */
    Flux<Appointment> findByPatientIdAndStartGreaterThanEqualAndStartLessThanEqual(
            String patientId, String from, String to);
    
    /**
     * Busca citas por estado
     * @param status Estado de la cita
     * @return Flux de citas con el estado indicado
     */
    Flux<Appointment> findByStatus(String status);
}