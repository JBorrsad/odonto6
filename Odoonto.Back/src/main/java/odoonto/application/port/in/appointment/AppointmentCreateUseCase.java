package odoonto.application.port.in.appointment;

import odoonto.application.dto.request.AppointmentCreateDTO;
import odoonto.application.dto.response.AppointmentDTO;

/**
 * Caso de uso para crear una cita
 */
public interface AppointmentCreateUseCase {
    AppointmentDTO createAppointment(AppointmentCreateDTO appointmentCreateDTO);
} 