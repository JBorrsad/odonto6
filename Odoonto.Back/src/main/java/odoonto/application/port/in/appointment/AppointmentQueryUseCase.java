package odoonto.application.port.in.appointment;

import odoonto.application.dto.response.AppointmentDTO;
import java.util.List;
import java.util.Optional;

/**
 * Caso de uso para consultar citas
 */
public interface AppointmentQueryUseCase {
    Optional<AppointmentDTO> findById(String appointmentId);
    List<AppointmentDTO> findAll();
    List<AppointmentDTO> findByPatientId(String patientId);
    List<AppointmentDTO> findByDoctorId(String doctorId);
} 