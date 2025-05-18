package odoonto.domain.repository;

import odoonto.domain.model.aggregates.Appointment;
import odoonto.domain.model.valueobjects.AppointmentStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para el agregado Appointment.
 * Define operaciones puras de dominio sin dependencias externas.
 */
public interface AppointmentRepository {
    
    /**
     * Busca una cita por su ID
     * @param id ID de la cita
     * @return Cita encontrada o Empty si no existe
     */
    Optional<Appointment> findById(String id);
    
    /**
     * Guarda una cita
     * @param appointment Cita a guardar
     * @return Cita guardada con su ID asignado
     */
    Appointment save(Appointment appointment);
    
    /**
     * Elimina una cita por su ID
     * @param id ID de la cita a eliminar
     */
    void deleteById(String id);
    
    /**
     * Obtiene todas las citas
     * @return Lista de citas
     */
    List<Appointment> findAll();
    
    /**
     * Busca citas por ID del paciente
     * @param patientId ID del paciente
     * @return Lista de citas del paciente
     */
    List<Appointment> findByPatientId(String patientId);
    
    /**
     * Busca citas por ID del doctor
     * @param doctorId ID del doctor
     * @return Lista de citas del doctor
     */
    List<Appointment> findByDoctorId(String doctorId);
    
    /**
     * Busca citas en un rango de fechas para un doctor específico
     * @param doctorId ID del doctor
     * @param from Fecha de inicio (en formato ISO-8601)
     * @param to Fecha de fin (en formato ISO-8601)
     * @return Lista de citas que cumplen el criterio
     */
    List<Appointment> findByDoctorIdAndDateRange(String doctorId, String from, String to);
    
    /**
     * Busca citas en un rango de fechas para un paciente específico
     * @param patientId ID del paciente
     * @param from Fecha de inicio (en formato ISO-8601)
     * @param to Fecha de fin (en formato ISO-8601)
     * @return Lista de citas que cumplen el criterio
     */
    List<Appointment> findByPatientIdAndDateRange(String patientId, String from, String to);
    
    /**
     * Busca citas por estado
     * @param status Estado de la cita
     * @return Lista de citas con el estado indicado
     */
    List<Appointment> findByStatus(AppointmentStatus status);
}