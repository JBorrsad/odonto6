package odoonto.presentation.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import odoonto.application.dto.response.MedicalRecordDTO;
import odoonto.application.port.in.patient.PatientOdontogramUseCase;
import odoonto.application.service.MedicalRecordService;
import odoonto.application.exceptions.MedicalRecordNotFoundException;
import reactor.core.publisher.Mono;

/**
 * Controlador REST para operaciones con historiales médicos
 */
@RestController
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final PatientOdontogramUseCase patientOdontogramUseCase;

    @Autowired
    public MedicalRecordController(
            MedicalRecordService medicalRecordService,
            PatientOdontogramUseCase patientOdontogramUseCase) {
        this.medicalRecordService = medicalRecordService;
        this.patientOdontogramUseCase = patientOdontogramUseCase;
    }
    
    // API sincrónica tradicional

    /**
     * Obtiene un historial médico por su ID
     * @param id ID del historial médico
     * @return DTO del historial médico
     */
    @GetMapping("/api/medical-records/{id}")
    public ResponseEntity<MedicalRecordDTO> getMedicalRecordById(@PathVariable String id) {
        MedicalRecordDTO medicalRecord = medicalRecordService.getMedicalRecordById(id);
        return ResponseEntity.ok(medicalRecord);
    }

    /**
     * Añade una entrada al historial médico
     * @param id ID del historial médico
     * @param entryDescription Descripción de la entrada
     * @return Historial médico actualizado
     */
    @PostMapping("/api/medical-records/{id}/entries")
    public ResponseEntity<MedicalRecordDTO> addMedicalEntry(
            @PathVariable String id,
            @RequestParam String entryDescription) {
        
        MedicalRecordDTO updated = medicalRecordService.addMedicalEntry(id, entryDescription);
        return ResponseEntity.status(HttpStatus.CREATED).body(updated);
    }

    /**
     * Elimina una entrada del historial médico
     * @param id ID del historial médico
     * @param entryId ID de la entrada
     * @return Historial médico actualizado
     */
    @DeleteMapping("/api/medical-records/{id}/entries/{entryId}")
    public ResponseEntity<MedicalRecordDTO> removeMedicalEntry(
            @PathVariable String id,
            @PathVariable String entryId) {
        
        MedicalRecordDTO updated = medicalRecordService.removeMedicalEntry(id, entryId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Obtiene el historial médico de un paciente
     * @param patientId ID del paciente
     * @return Historial médico del paciente
     */
    @GetMapping("/api/patients/{patientId}/medical-record")
    public ResponseEntity<Object> getPatientMedicalRecord(@PathVariable String patientId) {
        return ResponseEntity.ok(patientOdontogramUseCase.getPatientMedicalRecord(patientId));
    }
    
    // API reactiva
    
    /**
     * Obtiene un historial médico por su ID de forma reactiva
     * @param id ID del historial médico
     * @return Mono con el DTO del historial médico
     */
    @GetMapping(value = "/api/reactive/medical-records/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<MedicalRecordDTO> getMedicalRecordByIdReactive(@PathVariable String id) {
        return Mono.fromCallable(() -> medicalRecordService.getMedicalRecordById(id))
                .onErrorResume(MedicalRecordNotFoundException.class, e -> Mono.empty());
    }
    
    /**
     * Añade una entrada al historial médico de forma reactiva
     * @param id ID del historial médico
     * @param entryDescription Descripción de la entrada
     * @return Mono con el historial médico actualizado
     */
    @PostMapping(value = "/api/reactive/medical-records/{id}/entries", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MedicalRecordDTO> addMedicalEntryReactive(
            @PathVariable String id,
            @RequestParam String entryDescription) {
        
        return Mono.fromCallable(() -> medicalRecordService.addMedicalEntry(id, entryDescription))
                .onErrorResume(MedicalRecordNotFoundException.class, e -> Mono.empty());
    }
    
    /**
     * Elimina una entrada del historial médico de forma reactiva
     * @param id ID del historial médico
     * @param entryId ID de la entrada
     * @return Mono con el historial médico actualizado
     */
    @DeleteMapping(value = "/api/reactive/medical-records/{id}/entries/{entryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<MedicalRecordDTO> removeMedicalEntryReactive(
            @PathVariable String id,
            @PathVariable String entryId) {
        
        return Mono.fromCallable(() -> medicalRecordService.removeMedicalEntry(id, entryId))
                .onErrorResume(MedicalRecordNotFoundException.class, e -> Mono.empty());
    }
    
    /**
     * Obtiene el historial médico de un paciente de forma reactiva
     * @param patientId ID del paciente
     * @return Mono con el historial médico del paciente
     */
    @GetMapping(value = "/api/reactive/patients/{patientId}/medical-record", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Object> getPatientMedicalRecordReactive(@PathVariable String patientId) {
        return Mono.fromCallable(() -> patientOdontogramUseCase.getPatientMedicalRecord(patientId))
                .onErrorResume(Exception.class, e -> Mono.empty());
    }
} 