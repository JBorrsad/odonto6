// src/main/java/odoonto/domain/model/aggregates/Doctor.java
package odoonto.domain.model.aggregates;

import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.valueobjects.DoctorSchedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Agregado raíz que representa un doctor/médico en el sistema.
 */
public class Doctor {

    private String id;
    private String nombreCompleto;
    private String especialidad;
    private Map<DayOfWeek, DoctorSchedule> horarios;

    /**
     * Constructor sin argumentos necesario para frameworks
     */
    public Doctor() {
        this.horarios = new HashMap<>();
        initializeDefaultSchedule();
    }

    /**
     * Constructor para crear un nuevo doctor
     * @param nombreCompleto Nombre completo del doctor
     * @param especialidad Especialidad médica del doctor
     */
    public Doctor(String nombreCompleto, String especialidad) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new DomainException("El nombre del doctor no puede estar vacío");
        }
        this.nombreCompleto = nombreCompleto;
        this.especialidad = especialidad;
        this.horarios = new HashMap<>();
        initializeDefaultSchedule();
    }
    
    /**
     * Constructor con ID para reconstrucción desde persistencia
     */
    public Doctor(String id, String nombreCompleto, String especialidad) {
        this(nombreCompleto, especialidad);
        if (id == null || id.trim().isEmpty()) {
            throw new DomainException("El ID del doctor no puede estar vacío");
        }
        this.id = id;
    }
    
    /**
     * Inicializa un horario por defecto (lunes a viernes de 8:00 a 18:00)
     */
    private void initializeDefaultSchedule() {
        // Horario normal de lunes a viernes
        LocalTime defaultStartTime = LocalTime.of(8, 0);
        LocalTime defaultEndTime = LocalTime.of(18, 0);
        
        setSchedule(DayOfWeek.MONDAY, defaultStartTime, defaultEndTime, true);
        setSchedule(DayOfWeek.TUESDAY, defaultStartTime, defaultEndTime, true);
        setSchedule(DayOfWeek.WEDNESDAY, defaultStartTime, defaultEndTime, true);
        setSchedule(DayOfWeek.THURSDAY, defaultStartTime, defaultEndTime, true);
        setSchedule(DayOfWeek.FRIDAY, defaultStartTime, defaultEndTime, true);
        
        // Fines de semana no disponibles por defecto
        setSchedule(DayOfWeek.SATURDAY, null, null, false);
        setSchedule(DayOfWeek.SUNDAY, null, null, false);
    }
    
    /**
     * Establece el horario para un día específico
     * 
     * @param dayOfWeek Día de la semana
     * @param startTime Hora de inicio
     * @param endTime Hora de fin
     * @param available Si el doctor atiende ese día
     */
    public void setSchedule(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, boolean available) {
        DoctorSchedule schedule;
        
        if (available) {
            schedule = new DoctorSchedule(dayOfWeek, startTime, endTime, true);
        } else {
            schedule = new DoctorSchedule(dayOfWeek);
        }
        
        horarios.put(dayOfWeek, schedule);
    }
    
    /**
     * Verifica si el doctor está disponible en una fecha y hora específica
     * 
     * @param dateTime Fecha y hora
     * @param durationMinutes Duración en minutos
     * @return true si está disponible
     */
    public boolean isAvailable(LocalDateTime dateTime, int durationMinutes) {
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        LocalTime startTime = dateTime.toLocalTime();
        LocalTime endTime = startTime.plusMinutes(durationMinutes);
        
        DoctorSchedule schedule = horarios.get(dayOfWeek);
        
        if (schedule == null || !schedule.isAvailable()) {
            return false;
        }
        
        return schedule.isRangeInSchedule(startTime, endTime);
    }
    
    /**
     * Obtiene el horario para un día específico
     * 
     * @param dayOfWeek Día de la semana
     * @return Horario para ese día
     */
    public DoctorSchedule getScheduleForDay(DayOfWeek dayOfWeek) {
        return horarios.get(dayOfWeek);
    }
    
    /**
     * Verifica si el doctor atiende en una fecha específica
     * 
     * @param date Fecha a verificar
     * @return true si el doctor atiende ese día
     */
    public boolean worksOnDate(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        DoctorSchedule schedule = horarios.get(dayOfWeek);
        
        return schedule != null && schedule.isAvailable();
    }
    
    // Getters y setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public void setNombreCompleto(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new DomainException("El nombre del doctor no puede estar vacío");
        }
        this.nombreCompleto = nombreCompleto;
    }
    
    public String getEspecialidad() {
        return especialidad;
    }
    
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
    
    public Map<DayOfWeek, DoctorSchedule> getHorarios() {
        return new HashMap<>(horarios);
    }
    
    /**
     * Verifica si es un doctor válido para asignar citas
     * @return true si es válido
     */
    public boolean esValidoParaCitas() {
        return id != null && nombreCompleto != null && !nombreCompleto.trim().isEmpty();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Doctor doctor = (Doctor) o;
        return id != null && id.equals(doctor.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
