package odoonto.application.port.in.appointment;

import reactor.core.publisher.Mono;

/**
 * Caso de uso para cancelar una cita
 */
public interface AppointmentCancelUseCase {
    Mono<Void> cancelAppointment(String appointmentId);
} 