package odoonto.presentation.rest.controller;

import odoonto.application.dto.response.DoctorDTO;
import odoonto.application.dto.request.DoctorCreateDTO;
import odoonto.application.port.in.doctor.DoctorCreateUseCase;
import odoonto.application.port.in.doctor.DoctorQueryUseCase;
import odoonto.application.port.in.doctor.DoctorUpdateUseCase;
import odoonto.application.port.in.doctor.DoctorDeleteUseCase;
import odoonto.domain.exceptions.DomainException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador REST para operaciones con doctores
 */
@RestController
public class DoctorController {
    
    private final DoctorQueryUseCase doctorQueryUseCase;
    private final DoctorCreateUseCase doctorCreateUseCase;
    private final DoctorUpdateUseCase doctorUpdateUseCase;
    private final DoctorDeleteUseCase doctorDeleteUseCase;
    
    @Autowired
    public DoctorController(
            DoctorQueryUseCase doctorQueryUseCase,
            DoctorCreateUseCase doctorCreateUseCase,
            DoctorUpdateUseCase doctorUpdateUseCase,
            DoctorDeleteUseCase doctorDeleteUseCase) {
        this.doctorQueryUseCase = doctorQueryUseCase;
        this.doctorCreateUseCase = doctorCreateUseCase;
        this.doctorUpdateUseCase = doctorUpdateUseCase;
        this.doctorDeleteUseCase = doctorDeleteUseCase;
    }
    
    /**
     * Obtiene todos los doctores
     * @return Flux de DTOs de doctores
     */
    @GetMapping(value = "/api/doctors", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<DoctorDTO> getAllDoctors() {
        return doctorQueryUseCase.findAll();
    }
    
    /**
     * Obtiene un doctor por su ID
     * @param id ID del doctor
     * @return Mono con el DTO del doctor
     */
    @GetMapping(value = "/api/doctors/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<DoctorDTO> getDoctorById(@PathVariable String id) {
        return doctorQueryUseCase.findById(id)
                .onErrorResume(DomainException.class, e -> Mono.empty());
    }
    
    /**
     * Crea un nuevo doctor
     * @param createDTO DTO con datos del doctor
     * @return Mono con el DTO del doctor creado
     */
    @PostMapping(value = "/api/doctors", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DoctorDTO> createDoctor(@RequestBody DoctorCreateDTO createDTO) {
        return doctorCreateUseCase.createDoctor(createDTO);
    }
    
    /**
     * Actualiza un doctor existente
     * @param id ID del doctor a actualizar
     * @param updateDTO DTO con datos actualizados
     * @return Mono con el DTO del doctor actualizado
     */
    @PutMapping(value = "/api/doctors/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<DoctorDTO> updateDoctor(
            @PathVariable String id, 
            @RequestBody DoctorCreateDTO updateDTO) {
        return doctorUpdateUseCase.updateDoctor(id, updateDTO)
                .onErrorResume(DomainException.class, e -> Mono.empty());
    }
    
    /**
     * Elimina un doctor
     * @param id ID del doctor a eliminar
     * @return Mono vacío que completa cuando se elimina el doctor
     */
    @DeleteMapping("/api/doctors/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteDoctor(@PathVariable String id) {
        return doctorDeleteUseCase.deleteDoctor(id)
                .onErrorResume(DomainException.class, e -> Mono.empty());
    }
    
    /**
     * Busca doctores por especialidad
     * @param especialidad Especialidad a buscar
     * @return Flux de DTOs de doctores que coinciden con la búsqueda
     */
    @GetMapping(value = "/api/doctors/especialidad/{especialidad}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<DoctorDTO> getDoctorsByEspecialidad(@PathVariable String especialidad) {
        return doctorQueryUseCase.findByEspecialidad(especialidad);
    }
    
    /**
     * Busca doctores por nombre
     * @param query Texto a buscar en el nombre
     * @return Flux de DTOs de doctores que coinciden con la búsqueda
     */
    @GetMapping(value = "/api/doctors/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<DoctorDTO> searchDoctors(@RequestParam String query) {
        return doctorQueryUseCase.findByNombre(query);
    }
} 