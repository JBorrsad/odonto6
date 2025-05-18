package odoonto.application.port.in.appointment;

import odoonto.application.dto.request.AppointmentCreateDTO;
import odoonto.application.dto.response.AppointmentDTO;

/**
 * Caso de uso para actualizar una cita
 */
public interface AppointmentUpdateUseCase {
    AppointmentDTO updateAppointment(String appointmentId, AppointmentCreateDTO appointmentCreateDTO);
} 