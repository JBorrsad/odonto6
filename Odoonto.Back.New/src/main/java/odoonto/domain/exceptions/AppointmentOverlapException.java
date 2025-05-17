package odoonto.domain.exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Excepción que se lanza cuando se intenta agendar una cita superpuesta con otra.
 */
public class AppointmentOverlapException extends DomainException {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Constructor con mensaje de error
     * @param message Mensaje descriptivo del error
     */
    public AppointmentOverlapException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa
     * @param message Mensaje descriptivo del error
     * @param cause Causa original de la excepción
     */
    public AppointmentOverlapException(String message, Throwable cause) {
        super(message, cause);
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