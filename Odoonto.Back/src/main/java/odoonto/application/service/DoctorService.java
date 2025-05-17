package odoonto.application.service;

import odoonto.domain.model.aggregates.Doctor;
import odoonto.application.dto.response.DoctorDTO;
import odoonto.application.dto.request.DoctorCreateDTO;
import odoonto.application.exceptions.DoctorNotFoundException;
import odoonto.application.mapper.DoctorMapper;
import odoonto.domain.repository.DoctorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de aplicación para gestionar doctores
 */
@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository, DoctorMapper doctorMapper) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
    }

    /**
     * Obtiene todos los doctores
     * @return Lista de DTOs de doctores
     */
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll()
                .map(doctorMapper::toDTO)
                .collectList()
                .block();
    }

    /**
     * Obtiene un doctor por su ID
     * @param id ID del doctor
     * @return DTO del doctor
     * @throws DoctorNotFoundException si no se encuentra el doctor
     */
    public DoctorDTO getDoctorById(String id) {
        return doctorRepository.findById(id)
                .map(doctorMapper::toDTO)
                .blockOptional()
                .orElseThrow(() -> new DoctorNotFoundException(id));
    }

    /**
     * Crea un nuevo doctor
     * @param createDTO DTO con los datos del doctor
     * @return DTO del doctor creado
     */
    public DoctorDTO createDoctor(DoctorCreateDTO createDTO) {
        Doctor doctor = doctorMapper.toEntity(createDTO);
        Doctor savedDoctor = doctorRepository.save(doctor).block();
        return doctorMapper.toDTO(savedDoctor);
    }

    /**
     * Actualiza un doctor existente
     * @param id ID del doctor a actualizar
     * @param updateDTO DTO con los datos actualizados
     * @return DTO del doctor actualizado
     * @throws DoctorNotFoundException si no se encuentra el doctor
     */
    public DoctorDTO updateDoctor(String id, DoctorCreateDTO updateDTO) {
        return doctorRepository.findById(id)
                .map(existingDoctor -> {
                    doctorMapper.updateDoctorFromDTO(updateDTO, existingDoctor);
                    return doctorRepository.save(existingDoctor).block();
                })
                .map(doctorMapper::toDTO)
                .blockOptional()
                .orElseThrow(() -> new DoctorNotFoundException(id));
    }

    /**
     * Elimina un doctor
     * @param id ID del doctor a eliminar
     */
    public void deleteDoctor(String id) {
        doctorRepository.deleteById(id).block();
    }
} 