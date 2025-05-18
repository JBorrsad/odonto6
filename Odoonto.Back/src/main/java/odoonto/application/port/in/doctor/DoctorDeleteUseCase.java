package odoonto.application.port.in.doctor;

import reactor.core.publisher.Mono;

/**
 * Caso de uso para eliminar un doctor
 */
public interface DoctorDeleteUseCase {
    Mono<Void> deleteDoctor(String doctorId);
} 