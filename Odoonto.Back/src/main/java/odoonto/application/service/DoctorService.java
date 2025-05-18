package odoonto.application.service;

import odoonto.domain.model.aggregates.Doctor;
import odoonto.application.dto.response.DoctorDTO;
import odoonto.application.dto.request.DoctorCreateDTO;
import odoonto.application.exceptions.DoctorNotFoundException;
import odoonto.application.mapper.DoctorMapper;
import odoonto.application.port.out.ReactiveDoctorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Servicio de aplicación para gestionar doctores
 */
@Service
public class DoctorService {

    private final ReactiveDoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Autowired
    public DoctorService(ReactiveDoctorRepository doctorRepository, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
    }

    /**
     * Obtiene todos los doctores
     * @return Flux de DTOs de doctores
     */
    public Flux<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll()
                .map(doctorMapper::toDTO);
    }

    /**
     * Obtiene un doctor por su ID
     * @param id ID del doctor
     * @return Mono con el DTO del doctor
     * @throws DoctorNotFoundException si no se encuentra el doctor
     */
    public Mono<DoctorDTO> getDoctorById(String id) {
        return doctorRepository.findById(id)
                .map(doctorMapper::toDTO)
                .switchIfEmpty(Mono.error(new DoctorNotFoundException(id)));
    }

    /**
     * Crea un nuevo doctor
     * @param createDTO DTO con los datos del doctor
     * @return Mono con el DTO del doctor creado
     */
    public Mono<DoctorDTO> createDoctor(DoctorCreateDTO createDTO) {
        Doctor doctor = doctorMapper.toEntity(createDTO);
        return doctorRepository.save(doctor)
                .map(doctorMapper::toDTO);
    }

    /**
     * Actualiza un doctor existente
     * @param id ID del doctor a actualizar
     * @param updateDTO DTO con los datos actualizados
     * @return Mono con el DTO del doctor actualizado
     * @throws DoctorNotFoundException si no se encuentra el doctor
     */
    public Mono<DoctorDTO> updateDoctor(String id, DoctorCreateDTO updateDTO) {
        return doctorRepository.findById(id)
                .switchIfEmpty(Mono.error(new DoctorNotFoundException(id)))
                .flatMap(existingDoctor -> {
                    doctorMapper.updateDoctorFromDTO(updateDTO, existingDoctor);
                    return doctorRepository.save(existingDoctor);
                })
                .map(doctorMapper::toDTO);
    }

    /**
     * Elimina un doctor
     * @param id ID del doctor a eliminar
     * @return Mono<Void> que completa cuando se elimina el doctor
     */
    public Mono<Void> deleteDoctor(String id) {
        return doctorRepository.deleteById(id);
    }
} 