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

    /**
     * Obtiene un odontograma por su ID
     * @param id ID del odontograma
     * @return Mono con el DTO del odontograma
     */
    @GetMapping(value = "/api/odontograms/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<OdontogramDTO>> getOdontogramById(@PathVariable String id) {
        return odontogramService.getOdontogramById(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * Obtiene el odontograma de un paciente
     * @param patientId ID del paciente
     * @return Mono con el odontograma del paciente
     */
    @GetMapping(value = "/api/patients/{patientId}/odontogram", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Odontogram> getPatientOdontogram(@PathVariable String patientId) {
        return patientOdontogramUseCase.getPatientOdontogram(patientId);
    }
    
    /**
     * Registra una lesión en un diente
     * @param id ID del odontograma
     * @param toothNumber Número del diente
     * @param face Cara del diente
     * @param lesionType Tipo de lesión
     * @return Mono con el odontograma actualizado
     */
    @PostMapping(value = "/api/odontograms/{id}/teeth/{toothNumber}/faces/{face}/lesions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<OdontogramDTO>> addLesion(
            @PathVariable String id,
            @PathVariable int toothNumber,
            @PathVariable String face,
            @RequestParam String lesionType) {
        
        return odontogramService.addLesion(id, toothNumber, face, lesionType)
            .map(updated -> ResponseEntity.status(HttpStatus.CREATED).body(updated))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * Elimina una lesión de un diente
     * @param id ID del odontograma
     * @param toothNumber Número del diente
     * @param face Cara del diente
     * @return Mono con el odontograma actualizado
     */
    @DeleteMapping(value = "/api/odontograms/{id}/teeth/{toothNumber}/faces/{face}/lesions", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<OdontogramDTO>> removeLesion(
            @PathVariable String id,
            @PathVariable int toothNumber,
            @PathVariable String face) {
        
        return odontogramService.removeLesion(id, toothNumber, face)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * Registra un tratamiento en un diente
     * @param id ID del odontograma
     * @param toothNumber Número del diente
     * @param treatmentType Tipo de tratamiento
     * @return Mono con el odontograma actualizado
     */
    @PostMapping(value = "/api/odontograms/{id}/teeth/{toothNumber}/treatments", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<OdontogramDTO>> addTreatment(
            @PathVariable String id,
            @PathVariable int toothNumber,
            @RequestParam String treatmentType) {
        
        return odontogramService.addTreatment(id, toothNumber, treatmentType)
            .map(updated -> ResponseEntity.status(HttpStatus.CREATED).body(updated))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * Elimina un tratamiento de un diente
     * @param id ID del odontograma
     * @param toothNumber Número del diente
     * @return Mono con el odontograma actualizado
     */
    @DeleteMapping(value = "/api/odontograms/{id}/teeth/{toothNumber}/treatments", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<OdontogramDTO>> removeTreatment(
            @PathVariable String id,
            @PathVariable int toothNumber) {
        
        return odontogramService.removeTreatment(id, toothNumber)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
} 