package odoonto.api.domain.services;

import odoonto.api.domain.models.Appointment;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface AppointmentScheduler {
    /**
     * Reserva una cita si no hay solapamiento.
     * Devuelve la cita preparada (aún no guardada) o error si falla.
     */
    Mono<Appointment> schedule(String patientId,
                               String doctorId,
                               LocalDateTime start,
                               int slots);
}