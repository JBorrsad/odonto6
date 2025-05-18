package odoonto.application.service.doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.port.in.doctor.DoctorDeleteUseCase;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.repository.DoctorRepository;

import java.util.Optional;

/**
 * Implementación del caso de uso para eliminar un doctor
 */
@Service
public class DoctorDeleteService implements DoctorDeleteUseCase {

    private final DoctorRepository doctorRepository;

    @Autowired
    public DoctorDeleteService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public void deleteDoctor(String doctorId) {
        // Validaciones básicas
        if (doctorId == null || doctorId.trim().isEmpty()) {
            throw new DomainException("El ID del doctor no puede ser nulo o vacío");
        }
        
        // Verificar que el doctor existe antes de eliminarlo
        boolean exists = doctorRepository.findById(doctorId).isPresent();
        if (!exists) {
            throw new DomainException("No se encontró un doctor con el ID: " + doctorId);
        }
        
        // Eliminar el doctor
        doctorRepository.deleteById(doctorId);
    }
} 