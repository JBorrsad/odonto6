package odoonto.application.port.in.appointment;

import odoonto.application.dto.request.AppointmentCreateDTO;
import odoonto.application.dto.response.AppointmentDTO;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para crear una cita
 */
public interface AppointmentCreateUseCase {
    Mono<AppointmentDTO> createAppointment(AppointmentCreateDTO appointmentCreateDTO);
} 