package odoonto.domain.exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Excepción que se lanza cuando dos citas se superponen en el tiempo.
 */
public class AppointmentOverlapException extends DomainException {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private final String appointmentId1;
    private final String appointmentId2;
    private final String doctorId;
    
    /**
     * Constructor básico con mensaje de error
     * @param message Mensaje descriptivo del error
     */
    public AppointmentOverlapException(String message) {
        super(message);
        this.appointmentId1 = null;
        this.appointmentId2 = null;
        this.doctorId = null;
    }
    
    /**
     * Constructor para superposición de cita nueva con una existente
     * @param existingAppointmentId ID de la cita ya existente
     * @param doctorId ID del doctor
     * @param dateTimeString Representación en string de la fecha y hora
     */
    public AppointmentOverlapException(String existingAppointmentId, String doctorId, String dateTimeString) {
        super("La cita se superpone con la cita existente ID: " + existingAppointmentId + 
              " del doctor ID: " + doctorId + " en fecha/hora: " + dateTimeString);
        this.appointmentId1 = existingAppointmentId;
        this.appointmentId2 = null;
        this.doctorId = doctorId;
    }
    
    /**
     * Constructor para superposición entre dos citas existentes
     * @param firstAppointmentId ID de la primera cita
     * @param secondAppointmentId ID de la segunda cita
     * @param doctorIdValue ID del doctor
     */
    public AppointmentOverlapException(String firstAppointmentId, String secondAppointmentId, String doctorIdValue, boolean multipleAppointments) {
        super("Las citas ID: " + firstAppointmentId + " e ID: " + secondAppointmentId + 
              " del doctor ID: " + doctorIdValue + " se superponen");
        this.appointmentId1 = firstAppointmentId;
        this.appointmentId2 = secondAppointmentId;
        this.doctorId = doctorIdValue;
    }
    
    /**
     * @return ID de la primera cita en conflicto
     */
    public String getAppointmentId1() {
        return appointmentId1;
    }
    
    /**
     * @return ID de la segunda cita en conflicto (si existe)
     */
    public String getAppointmentId2() {
        return appointmentId2;
    }
    
    /**
     * @return ID del doctor involucrado
     */
    public String getDoctorId() {
        return doctorId;
    }
    
    /**
     * Verifica si la excepción implica a una cita específica
     * @param appointmentId ID de la cita a verificar
     * @return true si la cita está involucrada en el conflicto
     */
    public boolean involvesAppointment(String appointmentId) {
        return (appointmentId1 != null && appointmentId1.equals(appointmentId)) ||
               (appointmentId2 != null && appointmentId2.equals(appointmentId));
    }
    
    /**
     * Constructor específico para solapamiento con cita existente
     * @param proposedStart Inicio propuesto
     * @param proposedEnd Fin propuesto
     * @param existingStart Inicio existente
     * @param existingEnd Fin existente
     * @param doctorName Nombre del doctor (opcional)
     * @return AppointmentOverlapException con mensaje específico
     */
    public static AppointmentOverlapException forOverlap(
            LocalDateTime proposedStart,
            LocalDateTime proposedEnd,
            LocalDateTime existingStart,
            LocalDateTime existingEnd,
            String doctorName) {
        
        String doctorInfo = doctorName != null && !doctorName.isEmpty() 
                ? " con el doctor " + doctorName
                : "";
        
        return new AppointmentOverlapException(
                "La cita propuesta (" + FORMATTER.format(proposedStart) + 
                " a " + FORMATTER.format(proposedEnd) + ")" +
                " se solapa con una cita existente (" + 
                FORMATTER.format(existingStart) + " a " + 
                FORMATTER.format(existingEnd) + ")" + doctorInfo + ".");
    }
    
    /**
     * Constructor simplificado para solapamiento
     * @param doctorId ID del doctor
     * @return AppointmentOverlapException con mensaje simple
     */
    public static AppointmentOverlapException forDoctorBusy(String doctorId) {
        return new AppointmentOverlapException(
                "El doctor ya tiene otra cita programada en este horario.");
    }
} 