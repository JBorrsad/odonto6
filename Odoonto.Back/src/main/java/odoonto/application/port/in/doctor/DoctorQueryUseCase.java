package odoonto.application.port.in.doctor;

import odoonto.application.dto.response.DoctorDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para consultar doctores
 */
public interface DoctorQueryUseCase {
    Mono<DoctorDTO> findById(String doctorId);
    Flux<DoctorDTO> findAll();
    Flux<DoctorDTO> findByEspecialidad(String especialidad);
    Flux<DoctorDTO> findByNombre(String nombre);
} 