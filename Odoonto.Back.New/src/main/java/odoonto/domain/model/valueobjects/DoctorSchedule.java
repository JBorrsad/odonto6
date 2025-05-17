package odoonto.domain.model.valueobjects;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Objeto de valor que representa el horario de un doctor para un día específico de la semana.
 * Es inmutable y se valida en el constructor.
 */
public final class DoctorSchedule {
    private final DayOfWeek dayOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final boolean available;
    
    /**
     * Constructor para crear un horario de doctor válido.
     * 
     * @param dayOfWeek Día de la semana
     * @param startTime Hora de inicio de atención
     * @param endTime Hora de fin de atención
     * @param available Si el doctor atiende ese día
     */
    public DoctorSchedule(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, boolean available) {
        if (dayOfWeek == null) {
            throw new IllegalArgumentException("El día de la semana no puede ser nulo");
        }
        
        if (available) {
            if (startTime == null) {
                throw new IllegalArgumentException("La hora de inicio no puede ser nula si el día está disponible");
            }
            
            if (endTime == null) {
                throw new IllegalArgumentException("La hora de fin no puede ser nula si el día está disponible");
            }
            
            if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
                throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
            }
        }
        
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
    }
    
    /**
     * Constructor para días no disponibles
     * 
     * @param dayOfWeek Día de la semana
     */
    public DoctorSchedule(DayOfWeek dayOfWeek) {
        this(dayOfWeek, null, null, false);
    }
    
    /**
     * Verifica si un horario específico está dentro del horario de atención
     * 
     * @param time Hora a verificar
     * @return true si está dentro del horario
     */
    public boolean isTimeInSchedule(LocalTime time) {
        if (!available) {
            return false;
        }
        
        return !time.isBefore(startTime) && !time.isAfter(endTime);
    }
    
    /**
     * Verifica si el doctor está disponible en todo el rango horario
     * 
     * @param start Hora de inicio
     * @param end Hora de fin
     * @return true si todo el rango está dentro del horario
     */
    public boolean isRangeInSchedule(LocalTime start, LocalTime end) {
        if (!available) {
            return false;
        }
        
        return !start.isBefore(startTime) && !end.isAfter(endTime);
    }
    
    // Getters - No hay setters para mantener inmutabilidad
    
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        DoctorSchedule that = (DoctorSchedule) o;
        return available == that.available &&
               dayOfWeek == that.dayOfWeek &&
               Objects.equals(startTime, that.startTime) &&
               Objects.equals(endTime, that.endTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, startTime, endTime, available);
    }
    
    @Override
    public String toString() {
        if (!available) {
            return dayOfWeek + ": No disponible";
        }
        
        return dayOfWeek + ": " + startTime + " - " + endTime;
    }
} 