package odoonto.domain.model.aggregates;

import odoonto.domain.exceptions.DomainException;
import odoonto.domain.exceptions.InvalidAppointmentTimeException;
import odoonto.domain.model.valueobjects.AppointmentStatus;

import java.time.LocalDateTime;


/**
 * Agregado raíz que representa una cita médica en el sistema.
 */
public class Appointment {

    private String id;
    private String patientId;
    private String doctorId;
    private LocalDateTime dateTime;
    private int durationSlots; // 1 slot = 30 minutos
    private AppointmentStatus status;
    private String notes;

    /**
     * Constructor sin argumentos necesario para frameworks
     */
    public Appointment() {
        this.status = AppointmentStatus.PENDIENTE;
    }

    /**
     * Constructor principal para crear una nueva cita
     * @param patientId ID del paciente
     * @param doctorId ID del doctor
     * @param dateTime Fecha y hora de inicio
     * @param durationSlots Duración en slots de 30 minutos
     * @throws InvalidAppointmentTimeException Si el horario no es válido
     */
    public Appointment(String patientId, String doctorId, LocalDateTime dateTime, int durationSlots) {
        validateAppointmentTime(dateTime, durationSlots);
        validateIds(patientId, doctorId);
        
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateTime = dateTime;
        this.durationSlots = durationSlots;
        this.status = AppointmentStatus.PENDIENTE;
    }
    
    /**
     * Constructor con ID para reconstrucción desde persistencia
     */
    public Appointment(String id, String patientId, String doctorId, 
                       LocalDateTime dateTime, int durationSlots, 
                       AppointmentStatus status, String notes) {
        this(patientId, doctorId, dateTime, durationSlots);
        
        if (id == null || id.trim().isEmpty()) {
            throw new DomainException("El ID de la cita no puede estar vacío");
        }
        
        this.id = id;
        this.status = status != null ? status : AppointmentStatus.PENDIENTE;
        this.notes = notes;
    }
    
    /**
     * Valida que el horario de la cita sea válido (en slots de 30 minutos)
     */
    private void validateAppointmentTime(LocalDateTime dateTime, int durationSlots) {
        if (dateTime == null) {
            throw new InvalidAppointmentTimeException("La fecha y hora de inicio no puede ser nula");
        }
        
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new InvalidAppointmentTimeException("La fecha de la cita no puede ser en el pasado");
        }
        
        // Validar que la hora esté en intervalos de 30 minutos (XX:00 o XX:30)
        int minute = dateTime.getMinute();
        if (minute != 0 && minute != 30) {
            throw new InvalidAppointmentTimeException("La hora debe terminar en :00 o :30");
        }
        
        if (durationSlots <= 0 || durationSlots > 6) {
            throw new InvalidAppointmentTimeException("La duración debe ser entre 1 y 6 slots (30-180 minutos)");
        }
    }
    
    /**
     * Valida los IDs de paciente y doctor
     */
    private void validateIds(String patientId, String doctorId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new DomainException("El ID del paciente no puede estar vacío");
        }
        
        if (doctorId == null || doctorId.trim().isEmpty()) {
            throw new DomainException("El ID del doctor no puede estar vacío");
        }
    }
    
    /**
     * Confirma la cita
     * @return true si se pudo confirmar
     */
    public boolean confirmar() {
        if (status == AppointmentStatus.PENDIENTE) {
            status = AppointmentStatus.CONFIRMADA;
            return true;
        }
        return false;
    }
    
    /**
     * Cancela la cita
     * @param motivo Motivo de cancelación (opcional)
     * @return true si se pudo cancelar
     */
    public boolean cancelar(String motivo) {
        if (status.isCancelable()) {
            status = AppointmentStatus.CANCELADA;
            if (motivo != null && !motivo.trim().isEmpty()) {
                notes = motivo;
            }
            return true;
        }
        return false;
    }
    
    /**
     * Marca la cita como en proceso
     * @return true si se pudo iniciar
     */
    public boolean iniciar() {
        if (status == AppointmentStatus.CONFIRMADA) {
            status = AppointmentStatus.EN_PROCESO;
            return true;
        }
        return false;
    }
    
    /**
     * Marca la cita como completada
     * @return true si se pudo completar
     */
    public boolean completar() {
        if (status == AppointmentStatus.EN_PROCESO) {
            status = AppointmentStatus.COMPLETADA;
            return true;
        }
        return false;
    }
    
    /**
     * Calcula el momento de finalización de la cita
     * @return LocalDateTime de finalización
     */
    public LocalDateTime getEndDateTime() {
        return dateTime.plusMinutes(durationSlots * 30);
    }
    
    /**
     * Calcula la duración en minutos
     * @return Duración en minutos
     */
    public int getDuration() {
        return durationSlots * 30;
    }
    
    /**
     * Verifica si esta cita se solapa con otra
     * @param other Otra cita
     * @return true si hay solapamiento
     */
    public boolean overlapsWith(Appointment other) {
        if (other == null || !this.doctorId.equals(other.doctorId)) {
            return false;
        }
        
        LocalDateTime thisEnd = this.getEndDateTime();
        LocalDateTime otherEnd = other.getEndDateTime();
        
        // Hay solapamiento si:
        // 1. Esta cita comienza durante la otra
        // 2. Esta cita termina durante la otra
        // 3. Esta cita contiene completamente a la otra
        return (this.dateTime.isBefore(otherEnd) && this.dateTime.isAfter(other.dateTime)) ||
               (thisEnd.isAfter(other.dateTime) && thisEnd.isBefore(otherEnd)) ||
               (this.dateTime.isBefore(other.dateTime) && thisEnd.isAfter(otherEnd));
    }
    
    // Getters y setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        validateIds(patientId, this.doctorId != null ? this.doctorId : "");
        this.patientId = patientId;
    }
    
    public String getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(String doctorId) {
        validateIds(this.patientId != null ? this.patientId : "", doctorId);
        this.doctorId = doctorId;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(LocalDateTime dateTime) {
        validateAppointmentTime(dateTime, this.durationSlots);
        this.dateTime = dateTime;
    }
    
    public int getDurationSlots() {
        return durationSlots;
    }
    
    public void setDurationSlots(int durationSlots) {
        validateAppointmentTime(this.dateTime, durationSlots);
        this.durationSlots = durationSlots;
    }
    
    public AppointmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(AppointmentStatus status) {
        this.status = status != null ? status : AppointmentStatus.PENDIENTE;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Appointment that = (Appointment) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    /**
     * Para compatibilidad con el modelo antiguo
     */
    public LocalDateTime getStartAsLocalDateTime() {
        return dateTime;
    }
}
