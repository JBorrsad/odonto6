package odoonto.application.service.doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.request.DoctorCreateDTO;
import odoonto.application.dto.response.DoctorDTO;
import odoonto.application.mapper.DoctorMapper;
import odoonto.application.port.in.doctor.DoctorCreateUseCase;
import odoonto.application.port.out.ReactiveDoctorRepository;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.aggregates.Doctor;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para crear un doctor
 */
@Service
public class DoctorCreateService implements DoctorCreateUseCase {

    private final ReactiveDoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Autowired
    public DoctorCreateService(ReactiveDoctorRepository doctorRepository, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
    }

    @Override
    public Mono<DoctorDTO> createDoctor(DoctorCreateDTO doctorCreateDTO) {
        // Validaciones básicas
        if (doctorCreateDTO == null) {
            return Mono.error(new DomainException("Los datos del doctor no pueden ser nulos"));
        }
        
        // Convertir DTO a entidad de dominio
        Doctor doctor = doctorMapper.toEntity(doctorCreateDTO);
        
        // Guardar el doctor y convertir la respuesta a DTO
        return doctorRepository.save(doctor)
            .map(doctorMapper::toDTO);
    }
} 