package odoonto.application.service.doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.response.DoctorDTO;
import odoonto.application.mapper.DoctorMapper;
import odoonto.application.port.in.doctor.DoctorQueryUseCase;
import odoonto.domain.model.valueobjects.Specialty;
import odoonto.domain.repository.DoctorRepository;
import odoonto.application.port.out.ReactiveDoctorRepository;

import java.util.List;
import java.util.Optional;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

/**
 * Implementación del caso de uso para consultar doctores
 */
@Service
public class DoctorQueryService implements DoctorQueryUseCase {

    private final ReactiveDoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Autowired
    public DoctorQueryService(ReactiveDoctorRepository doctorRepository, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
    }

    @Override
    public Mono<DoctorDTO> findById(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            return Mono.empty();
        }
        
        return doctorRepository.findById(doctorId)
            .map(doctorMapper::toDTO);
    }

    @Override
    public Flux<DoctorDTO> findAll() {
        return doctorRepository.findAll()
            .map(doctorMapper::toDTO);
    }

    @Override
    public Flux<DoctorDTO> findByEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.trim().isEmpty()) {
            return Flux.empty();
        }
        
        Specialty specialty = Specialty.valueOf(especialidad);
        return doctorRepository.findByEspecialidad(specialty)
            .map(doctorMapper::toDTO);
    }

    @Override
    public Flux<DoctorDTO> findByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return Flux.empty();
        }
        
        return doctorRepository.findByNombreCompletoContaining(nombre)
            .map(doctorMapper::toDTO);
    }
} 