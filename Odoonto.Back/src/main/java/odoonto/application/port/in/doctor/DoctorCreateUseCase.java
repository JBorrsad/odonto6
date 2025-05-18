package odoonto.application.port.in.doctor;

import odoonto.application.dto.request.DoctorCreateDTO;
import odoonto.application.dto.response.DoctorDTO;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para crear un doctor
 */
public interface DoctorCreateUseCase {
    Mono<DoctorDTO> createDoctor(DoctorCreateDTO doctorCreateDTO);
} 