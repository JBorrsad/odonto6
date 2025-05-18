package odoonto.presentation.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import odoonto.application.dto.response.OdontogramDTO;
import odoonto.application.port.in.patient.PatientOdontogramUseCase;
import odoonto.application.service.OdontogramService;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.application.exceptions.OdontogramNotFoundException;
import reactor.core.publisher.Mono;

/**
 * Controlador REST para operaciones con odontogramas
 */
@RestController
public class OdontogramController {
    
    private final OdontogramService odontogramService;
    private final PatientOdontogramUseCase patientOdontogramUseCase;
    
    @Autowired
    public OdontogramController(
            OdontogramService odontogramService,
            PatientOdontogramUseCase patientOdontogramUseCase) {
        this.odontogramService = odontogramService;
        this.patientOdontogramUseCase = patientOdontogramUseCase;
    }

    // API sincrónica tradicional
    
    /**
     * Obtiene un odontograma por su ID
     * @param id ID del odontograma
     * @return DTO del odontograma
     */
    @GetMapping("/api/odontograms/{id}")
    public ResponseEntity<OdontogramDTO> getOdontogramById(@PathVariable String id) {
        OdontogramDTO odontogram = odontogramService.getOdontogramById(id);
        return ResponseEntity.ok(odontogram);
    }
    
    /**
     * Obtiene el odontograma de un paciente
     * @param patientId ID del paciente
     * @return Odontograma del paciente
     */
    @GetMapping("/api/patients/{patientId}/odontogram")
    public ResponseEntity<Object> getPatientOdontogram(@PathVariable String patientId) {
        return ResponseEntity.ok(patientOdontogramUseCase.getPatientOdontogram(patientId));
    }
    
    /**
     * Registra una lesión en un diente
     * @param id ID del odontograma
     * @param toothNumber Número del diente
     * @param face Cara del diente
     * @param lesionType Tipo de lesión
     * @return Odontograma actualizado
     */
    @PostMapping("/api/odontograms/{id}/teeth/{toothNumber}/faces/{face}/lesions")
    public ResponseEntity<OdontogramDTO> addLesion(
            @PathVariable String id,
            @PathVariable int toothNumber,
            @PathVariable String face,
            @RequestParam String lesionType) {
        
        OdontogramDTO updated = odontogramService.addLesion(id, toothNumber, face, lesionType);
        return ResponseEntity.status(HttpStatus.CREATED).body(updated);
    }
    
    /**
     * Elimina una lesión de un diente
     * @param id ID del odontograma
     * @param toothNumber Número del diente
     * @param face Cara del diente
     * @return Odontograma actualizado
     */
    @DeleteMapping("/api/odontograms/{id}/teeth/{toothNumber}/faces/{face}/lesions")
    public ResponseEntity<OdontogramDTO> removeLesion(
            @PathVariable String id,
            @PathVariable int toothNumber,
            @PathVariable String face) {
        
        OdontogramDTO updated = odontogramService.removeLesion(id, toothNumber, face);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Registra un tratamiento en un diente
     * @param id ID del odontograma
     * @param toothNumber Número del diente
     * @param treatmentType Tipo de tratamiento
     * @return Odontograma actualizado
     */
    @PostMapping("/api/odontograms/{id}/teeth/{toothNumber}/treatments")
    public ResponseEntity<OdontogramDTO> addTreatment(
            @PathVariable String id,
            @PathVariable int toothNumber,
            @RequestParam String treatmentType) {
        
        OdontogramDTO updated = odontogramService.addTreatment(id, toothNumber, treatmentType);
        return ResponseEntity.status(HttpStatus.CREATED).body(updated);
    }
    
    /**
     * Elimina un tratamiento de un diente
     * @param id ID del odontograma
     * @param toothNumber Número del diente
     * @return Odontograma actualizado
     */
    @DeleteMapping("/api/odontograms/{id}/teeth/{toothNumber}/treatments")
    public ResponseEntity<OdontogramDTO> removeTreatment(
            @PathVariable String id,
            @PathVariable int toothNumber) {
        
        OdontogramDTO updated = odontogramService.removeTreatment(id, toothNumber);
        return ResponseEntity.ok(updated);
    }
    
    // API reactiva
    
    /**
     * Obtiene un odontograma por su ID de forma reactiva
     * @param id ID del odontograma
     * @return Mono con el DTO del odontograma
     */
    @GetMapping(value = "/api/reactive/odontograms/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<OdontogramDTO> getOdontogramByIdReactive(@PathVariable String id) {
        return odontogramService.getOdontogramByIdReactive(id);
    }
    
    /**
     * Obtiene el odontograma de un paciente de forma reactiva
     * @param patientId ID del paciente
     * @return Mono con el odontograma del paciente
     */
    @GetMapping(value = "/api/reactive/patients/{patientId}/odontogram", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Odontogram> getPatientOdontogramReactive(@PathVariable String patientId) {
        return odontogramService.getPatientOdontogramReactive(patientId);
    }
    
    /**
     * Registra una lesión en un diente de forma reactiva
     * @param id ID del odontograma
     * @param toothNumber Número del diente
     * @param face Cara del diente
     * @param lesionType Tipo de lesión
     * @return Mono con el odontograma actualizado
     */
    @PostMapping(value = "/api/reactive/odontograms/{id}/teeth/{toothNumber}/faces/{face}/lesions", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<OdontogramDTO> addLesionReactive(
            @PathVariable String id,
            @PathVariable int toothNumber,
            @PathVariable String face,
            @RequestParam String lesionType) {
        
        return odontogramService.addLesionReactive(id, toothNumber, face, lesionType);
    }
    
    /**
     * Elimina una lesión de un diente de forma reactiva
     * @param id ID del odontograma
     * @param toothNumber Número del diente
     * @param face Cara del diente
     * @return Mono con el odontograma actualizado
     */
    @DeleteMapping(value = "/api/reactive/odontograms/{id}/teeth/{toothNumber}/faces/{face}/lesions", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<OdontogramDTO> removeLesionReactive(
            @PathVariable String id,
            @PathVariable int toothNumber,
            @PathVariable String face) {
        
        return odontogramService.removeLesionReactive(id, toothNumber, face);
    }
    
    /**
     * Registra un tratamiento en un diente de forma reactiva
     * @param id ID del odontograma
     * @param toothNumber Número del diente
     * @param treatmentType Tipo de tratamiento
     * @return Mono con el odontograma actualizado
     */
    @PostMapping(value = "/api/reactive/odontograms/{id}/teeth/{toothNumber}/treatments", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OdontogramDTO> addTreatmentReactive(
            @PathVariable String id,
            @PathVariable int toothNumber,
            @RequestParam String treatmentType) {
        
        return Mono.fromCallable(() -> odontogramService.addTreatment(id, toothNumber, treatmentType))
                .onErrorResume(OdontogramNotFoundException.class, e -> Mono.empty());
    }
    
    /**
     * Elimina un tratamiento de un diente de forma reactiva
     * @param id ID del odontograma
     * @param toothNumber Número del diente
     * @return Mono con el odontograma actualizado
     */
    @DeleteMapping(value = "/api/reactive/odontograms/{id}/teeth/{toothNumber}/treatments", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<OdontogramDTO> removeTreatmentReactive(
            @PathVariable String id,
            @PathVariable int toothNumber) {
        
        return Mono.fromCallable(() -> odontogramService.removeTreatment(id, toothNumber))
                .onErrorResume(OdontogramNotFoundException.class, e -> Mono.empty());
    }
} 