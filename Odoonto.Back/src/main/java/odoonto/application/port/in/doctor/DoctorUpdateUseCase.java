package odoonto.application.port.in.doctor;

import odoonto.application.dto.request.DoctorCreateDTO;
import odoonto.application.dto.response.DoctorDTO;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para actualizar un doctor
 */
public interface DoctorUpdateUseCase {
    Mono<DoctorDTO> updateDoctor(String doctorId, DoctorCreateDTO doctorCreateDTO);
} 