package odoonto.application.port.in.appointment;

/**
 * Caso de uso para cancelar una cita
 */
public interface AppointmentCancelUseCase {
    void cancelAppointment(String appointmentId);
} 