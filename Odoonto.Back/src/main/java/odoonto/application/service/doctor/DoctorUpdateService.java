package odoonto.application.service.doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.request.DoctorCreateDTO;
import odoonto.application.dto.response.DoctorDTO;
import odoonto.application.mapper.DoctorMapper;
import odoonto.application.port.in.doctor.DoctorUpdateUseCase;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.aggregates.Doctor;
import odoonto.domain.repository.DoctorRepository;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para actualizar un doctor
 */
@Service
public class DoctorUpdateService implements DoctorUpdateUseCase {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Autowired
    public DoctorUpdateService(DoctorRepository doctorRepository, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
    }

    @Override
    public DoctorDTO updateDoctor(String doctorId, DoctorCreateDTO doctorCreateDTO) {
        // Validaciones básicas
        if (doctorId == null || doctorId.trim().isEmpty()) {
            throw new DomainException("El ID del doctor no puede ser nulo o vacío");
        }
        
        if (doctorCreateDTO == null) {
            throw new DomainException("Los datos del doctor no pueden ser nulos");
        }
        
        // Buscar el doctor, actualizarlo y devolver el resultado
        return doctorRepository.findById(doctorId)
            .switchIfEmpty(Mono.error(new DomainException("No se encontró un doctor con el ID: " + doctorId)))
            .flatMap(existingDoctor -> {
                // Actualizar los campos del doctor
                if (doctorCreateDTO.getNombreCompleto() != null && !doctorCreateDTO.getNombreCompleto().trim().isEmpty()) {
                    existingDoctor.setNombreCompleto(doctorCreateDTO.getNombreCompleto());
                }
                
                if (doctorCreateDTO.getEspecialidad() != null && !doctorCreateDTO.getEspecialidad().trim().isEmpty()) {
                    existingDoctor.setEspecialidad(doctorCreateDTO.getEspecialidad());
                }
                
                // Guardar los cambios
                return doctorRepository.save(existingDoctor);
            })
            .map(doctorMapper::toDTO)
            .block(); // Bloquear para obtener el resultado (en un entorno real, sería mejor mantenerlo reactivo)
    }
} 