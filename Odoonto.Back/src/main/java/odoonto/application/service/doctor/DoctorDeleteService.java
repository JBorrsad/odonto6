package odoonto.application.service.doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.port.in.doctor.DoctorDeleteUseCase;
import odoonto.application.port.out.ReactiveDoctorRepository;
import odoonto.domain.exceptions.DomainException;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para eliminar un doctor
 */
@Service
public class DoctorDeleteService implements DoctorDeleteUseCase {

    private final ReactiveDoctorRepository doctorRepository;

    @Autowired
    public DoctorDeleteService(ReactiveDoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public Mono<Void> deleteDoctor(String doctorId) {
        // Validaciones básicas
        if (doctorId == null || doctorId.trim().isEmpty()) {
            return Mono.error(new DomainException("El ID del doctor no puede ser nulo o vacío"));
        }
        
        // Verificar que el doctor existe antes de eliminarlo
        return doctorRepository.findById(doctorId)
            .switchIfEmpty(Mono.error(new DomainException("No se encontró un doctor con el ID: " + doctorId)))
            .flatMap(doctor -> doctorRepository.deleteById(doctorId));
    }
} 