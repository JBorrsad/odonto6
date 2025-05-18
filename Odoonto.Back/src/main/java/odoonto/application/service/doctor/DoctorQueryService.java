package odoonto.application.service.doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.response.DoctorDTO;
import odoonto.application.mapper.DoctorMapper;
import odoonto.application.port.in.doctor.DoctorQueryUseCase;
import odoonto.domain.repository.DoctorRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del caso de uso para consultar doctores
 */
@Service
public class DoctorQueryService implements DoctorQueryUseCase {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Autowired
    public DoctorQueryService(DoctorRepository doctorRepository, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
    }

    @Override
    public Optional<DoctorDTO> findById(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return Optional.ofNullable(
            doctorRepository.findById(doctorId)
                .map(doctorMapper::toDTO)
                .block() // Bloquear para obtener el resultado
        );
    }

    @Override
    public List<DoctorDTO> findAll() {
        return doctorRepository.findAll()
            .map(doctorMapper::toDTO)
            .collectList()
            .block(); // Bloquear para obtener el resultado
    }

    @Override
    public List<DoctorDTO> findByEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.trim().isEmpty()) {
            return List.of();
        }
        
        return doctorRepository.findByEspecialidad(especialidad)
            .map(doctorMapper::toDTO)
            .collectList()
            .block(); // Bloquear para obtener el resultado
    }

    @Override
    public List<DoctorDTO> findByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return List.of();
        }
        
        return doctorRepository.findByNombreCompletoContaining(nombre)
            .map(doctorMapper::toDTO)
            .collectList()
            .block(); // Bloquear para obtener el resultado
    }
} 