package odoonto.presentation.rest.controller;

import odoonto.application.dto.response.DoctorDTO;
import odoonto.application.dto.request.DoctorCreateDTO;
import odoonto.application.service.DoctorService;
import odoonto.application.exceptions.DoctorNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para operaciones con doctores
 */
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    
    private final DoctorService doctorService;
    
    @Autowired
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }
    
    /**
     * Obtiene todos los doctores
     * @return Lista de DTOs de doctores
     */
    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }
    
    /**
     * Obtiene un doctor por su ID
     * @param id ID del doctor
     * @return DTO del doctor o 404 si no existe
     */
    @GetMapping("/{id}")
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
    @PostMapping
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
    @PutMapping("/{id}")
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Busca doctores por especialidad (filtrado en memoria)
     * @param especialidad Especialidad a buscar
     * @return Lista de DTOs de doctores que coinciden con la búsqueda
     */
    @GetMapping("/especialidad/{especialidad}")
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
    @GetMapping("/search")
    public ResponseEntity<List<DoctorDTO>> searchDoctors(@RequestParam String query) {
        String queryLower = query.toLowerCase();
        List<DoctorDTO> doctors = doctorService.getAllDoctors().stream()
            .filter(d -> d.getNombreCompleto().toLowerCase().contains(queryLower))
            .collect(Collectors.toList());
        return ResponseEntity.ok(doctors);
    }
} 