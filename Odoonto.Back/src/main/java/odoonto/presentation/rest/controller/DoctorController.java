package odoonto.presentation.rest.controller;

import odoonto.application.dto.response.DoctorDTO;
import odoonto.application.dto.request.DoctorCreateDTO;
import odoonto.application.service.DoctorService;
import odoonto.application.exceptions.DoctorNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para operaciones con doctores
 */
@RestController
public class DoctorController {
    
    private final DoctorService doctorService;
    
    @Autowired
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }
    
    // API sincrónica tradicional
    
    /**
     * Obtiene todos los doctores
     * @return Lista de DTOs de doctores
     */
    @GetMapping("/api/doctors")
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }
    
    /**
     * Obtiene un doctor por su ID
     * @param id ID del doctor
     * @return DTO del doctor o 404 si no existe
     */
    @GetMapping("/api/doctors/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable String id) {
        try {
            DoctorDTO doctor = doctorService.getDoctorById(id);
            return ResponseEntity.ok(doctor);
        } catch (DoctorNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Crea un nuevo doctor
     * @param createDTO DTO con datos del doctor
     * @return DTO del doctor creado
     */
    @PostMapping("/api/doctors")
    public ResponseEntity<DoctorDTO> createDoctor(@RequestBody DoctorCreateDTO createDTO) {
        DoctorDTO created = doctorService.createDoctor(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * Actualiza un doctor existente
     * @param id ID del doctor a actualizar
     * @param updateDTO DTO con datos actualizados
     * @return DTO del doctor actualizado o 404 si no existe
     */
    @PutMapping("/api/doctors/{id}")
    public ResponseEntity<DoctorDTO> updateDoctor(
            @PathVariable String id, 
            @RequestBody DoctorCreateDTO updateDTO) {
        try {
            DoctorDTO updated = doctorService.updateDoctor(id, updateDTO);
            return ResponseEntity.ok(updated);
        } catch (DoctorNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Elimina un doctor
     * @param id ID del doctor a eliminar
     * @return 204 No Content si se eliminó correctamente
     */
    @DeleteMapping("/api/doctors/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Busca doctores por especialidad (filtrado en memoria)
     * @param especialidad Especialidad a buscar
     * @return Lista de DTOs de doctores que coinciden con la búsqueda
     */
    @GetMapping("/api/doctors/especialidad/{especialidad}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByEspecialidad(
            @PathVariable String especialidad) {
        String especialidadLower = especialidad.toLowerCase();
        List<DoctorDTO> doctors = doctorService.getAllDoctors().stream()
            .filter(d -> d.getEspecialidad().toLowerCase().contains(especialidadLower))
            .collect(Collectors.toList());
        return ResponseEntity.ok(doctors);
    }
    
    /**
     * Busca doctores por nombre (filtrado en memoria)
     * @param query Texto a buscar en el nombre
     * @return Lista de DTOs de doctores que coinciden con la búsqueda
     */
    @GetMapping("/api/doctors/search")
    public ResponseEntity<List<DoctorDTO>> searchDoctors(@RequestParam String query) {
        String queryLower = query.toLowerCase();
        List<DoctorDTO> doctors = doctorService.getAllDoctors().stream()
            .filter(d -> d.getNombreCompleto().toLowerCase().contains(queryLower))
            .collect(Collectors.toList());
        return ResponseEntity.ok(doctors);
    }
    
    // API reactiva
    
    /**
     * Obtiene todos los doctores de forma reactiva
     * @return Flux de DTOs de doctores
     */
    @GetMapping(value = "/api/reactive/doctors", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<DoctorDTO> getAllDoctorsReactive() {
        return Flux.fromIterable(doctorService.getAllDoctors());
    }
    
    /**
     * Obtiene un doctor por su ID de forma reactiva
     * @param id ID del doctor
     * @return Mono con el DTO del doctor
     */
    @GetMapping(value = "/api/reactive/doctors/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<DoctorDTO> getDoctorByIdReactive(@PathVariable String id) {
        return Mono.justOrEmpty(doctorService.getDoctorById(id))
                .onErrorResume(DoctorNotFoundException.class, e -> Mono.empty());
    }
    
    /**
     * Crea un nuevo doctor de forma reactiva
     * @param createDTO DTO con datos del doctor
     * @return Mono con el DTO del doctor creado
     */
    @PostMapping(value = "/api/reactive/doctors", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DoctorDTO> createDoctorReactive(@RequestBody DoctorCreateDTO createDTO) {
        return Mono.just(doctorService.createDoctor(createDTO));
    }
    
    /**
     * Actualiza un doctor existente de forma reactiva
     * @param id ID del doctor a actualizar
     * @param updateDTO DTO con datos actualizados
     * @return Mono con el DTO del doctor actualizado
     */
    @PutMapping(value = "/api/reactive/doctors/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<DoctorDTO> updateDoctorReactive(
            @PathVariable String id, 
            @RequestBody DoctorCreateDTO updateDTO) {
        return Mono.justOrEmpty(doctorService.updateDoctor(id, updateDTO))
                .onErrorResume(DoctorNotFoundException.class, e -> Mono.empty());
    }
    
    /**
     * Elimina un doctor de forma reactiva
     * @param id ID del doctor a eliminar
     * @return Mono vacío que completa cuando se elimina el doctor
     */
    @DeleteMapping("/api/reactive/doctors/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteDoctorReactive(@PathVariable String id) {
        return Mono.fromRunnable(() -> doctorService.deleteDoctor(id));
    }
    
    /**
     * Busca doctores por especialidad de forma reactiva
     * @param especialidad Especialidad a buscar
     * @return Flux de DTOs de doctores que coinciden con la búsqueda
     */
    @GetMapping(value = "/api/reactive/doctors/especialidad/{especialidad}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<DoctorDTO> getDoctorsByEspecialidadReactive(@PathVariable String especialidad) {
        String especialidadLower = especialidad.toLowerCase();
        return Flux.fromIterable(doctorService.getAllDoctors())
                .filter(d -> d.getEspecialidad().toLowerCase().contains(especialidadLower));
    }
    
    /**
     * Busca doctores por nombre de forma reactiva
     * @param query Texto a buscar en el nombre
     * @return Flux de DTOs de doctores que coinciden con la búsqueda
     */
    @GetMapping(value = "/api/reactive/doctors/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<DoctorDTO> searchDoctorsReactive(@RequestParam String query) {
        String queryLower = query.toLowerCase();
        return Flux.fromIterable(doctorService.getAllDoctors())
                .filter(d -> d.getNombreCompleto().toLowerCase().contains(queryLower));
    }
} 