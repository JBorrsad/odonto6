package odoonto.application.port.in.appointment;

import odoonto.application.dto.response.AppointmentDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para consultar citas
 */
public interface AppointmentQueryUseCase {
    Mono<AppointmentDTO> findById(String appointmentId);
    Flux<AppointmentDTO> findAll();
    Flux<AppointmentDTO> findByPatientId(String patientId);
    Flux<AppointmentDTO> findByDoctorId(String doctorId);
} 