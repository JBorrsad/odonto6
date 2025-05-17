package odoonto.application.port.out;

import odoonto.domain.model.aggregates.Appointment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para el repositorio de citas
 * Abstrae el acceso a datos para la entidad Appointment
 */
public interface AppointmentRepositoryPort {
    
    /**
     * Guarda una cita en el repositorio
     * @param appointment Cita a guardar
     * @return Cita con posibles cambios (como ID asignado)
     */
    Appointment save(Appointment appointment);
    
    /**
     * Busca una cita por su ID
     * @param id ID de la cita
     * @return Optional con la cita si existe
     */
    Optional<Appointment> findById(String id);
    
    /**
     * Obtiene todas las citas
     * @return Lista de citas
     */
    List<Appointment> findAll();
    
    /**
     * Elimina una cita por su ID
     * @param id ID de la cita a eliminar
     */
    void deleteById(String id);
    
    /**
     * Busca citas por ID de paciente
     * @param patientId ID del paciente
     * @return Lista de citas del paciente
     */
    List<Appointment> findByPatientId(String patientId);
    
    /**
     * Busca citas por ID de doctor
     * @param doctorId ID del doctor
     * @return Lista de citas del doctor
     */
    List<Appointment> findByDoctorId(String doctorId);
    
    /**
     * Busca citas para un doctor en un rango de fechas
     * @param doctorId ID del doctor
     * @param start Fecha y hora de inicio
     * @param end Fecha y hora de fin
     * @return Lista de citas en ese rango
     */
    List<Appointment> findByDoctorIdAndDateTimeRange(String doctorId, LocalDateTime start, LocalDateTime end);
    
    /**
     * Busca citas para un paciente en un rango de fechas
     * @param patientId ID del paciente
     * @param start Fecha y hora de inicio
     * @param end Fecha y hora de fin
     * @return Lista de citas en ese rango
     */
    List<Appointment> findByPatientIdAndDateTimeRange(String patientId, LocalDateTime start, LocalDateTime end);
    
    /**
     * Busca citas para una fecha específica
     * @param date Fecha a buscar
     * @return Lista de citas en esa fecha
     */
    List<Appointment> findByDate(LocalDate date);
} 