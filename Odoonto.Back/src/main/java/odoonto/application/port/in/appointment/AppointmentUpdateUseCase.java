package odoonto.application.port.in.appointment;

import odoonto.application.dto.request.AppointmentCreateDTO;
import odoonto.application.dto.response.AppointmentDTO;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para actualizar una cita
 */
public interface AppointmentUpdateUseCase {
    Mono<AppointmentDTO> updateAppointment(String appointmentId, AppointmentCreateDTO appointmentCreateDTO);
} 