package odoonto.presentation.rest.controller;

import odoonto.application.dto.response.OdontogramDTO;
import odoonto.application.dto.request.LesionCreateDTO;
import odoonto.application.service.OdontogramService;
import odoonto.application.exceptions.PatientNotFoundException;
import odoonto.domain.exceptions.DuplicateLesionException;
import odoonto.domain.exceptions.InvalidToothFaceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para operaciones con odontogramas
 */
@RestController
@RequestMapping("/api/odontograms")
public class OdontogramController {
    
    private final OdontogramService odontogramService;
    
    @Autowired
    public OdontogramController(OdontogramService odontogramService) {
        this.odontogramService = odontogramService;
    }
    
    /**
     * Obtiene el odontograma de un paciente
     * @param patientId ID del paciente
     * @return DTO del odontograma o 404 si no existe el paciente
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<OdontogramDTO> getOdontogramByPatientId(@PathVariable String patientId) {
        try {
            OdontogramDTO odontogram = odontogramService.getOdontogramByPatientId(patientId);
            return ResponseEntity.ok(odontogram);
        } catch (PatientNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Añade una lesión al odontograma
     * @param patientId ID del paciente
     * @param lesionDTO DTO con datos de la lesión
     * @return DTO del odontograma actualizado
     */
    @PostMapping("/patient/{patientId}/lesion")
    public ResponseEntity<?> addLesion(
            @PathVariable String patientId,
            @RequestBody LesionCreateDTO lesionDTO) {
        try {
            OdontogramDTO updated = odontogramService.addLesions(patientId, List.of(lesionDTO));
            return ResponseEntity.ok(updated);
        } catch (PatientNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (DuplicateLesionException | InvalidToothFaceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Añade múltiples lesiones al odontograma
     * @param patientId ID del paciente
     * @param lesions Lista de DTOs con datos de las lesiones
     * @return DTO del odontograma actualizado
     */
    @PostMapping("/patient/{patientId}/lesions")
    public ResponseEntity<?> addLesions(
            @PathVariable String patientId,
            @RequestBody List<LesionCreateDTO> lesions) {
        try {
            OdontogramDTO updated = odontogramService.addLesions(patientId, lesions);
            return ResponseEntity.ok(updated);
        } catch (PatientNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (DuplicateLesionException | InvalidToothFaceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Elimina una lesión del odontograma
     * @param patientId ID del paciente
     * @param lesionDTO DTO con datos de la lesión a eliminar
     * @return DTO del odontograma actualizado
     */
    @DeleteMapping("/patient/{patientId}/lesion")
    public ResponseEntity<?> removeLesion(
            @PathVariable String patientId,
            @RequestBody LesionCreateDTO lesionDTO) {
        try {
            OdontogramDTO updated = odontogramService.removeLesions(patientId, List.of(lesionDTO));
            return ResponseEntity.ok(updated);
        } catch (PatientNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidToothFaceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Elimina múltiples lesiones del odontograma
     * @param patientId ID del paciente
     * @param lesions Lista de DTOs con datos de las lesiones a eliminar
     * @return DTO del odontograma actualizado
     */
    @DeleteMapping("/patient/{patientId}/lesions")
    public ResponseEntity<?> removeLesions(
            @PathVariable String patientId,
            @RequestBody List<LesionCreateDTO> lesions) {
        try {
            OdontogramDTO updated = odontogramService.removeLesions(patientId, lesions);
            return ResponseEntity.ok(updated);
        } catch (PatientNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidToothFaceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 