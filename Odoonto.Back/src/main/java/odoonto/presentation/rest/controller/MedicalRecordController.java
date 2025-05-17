package odoonto.presentation.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import odoonto.application.dto.response.MedicalRecordDTO;
import odoonto.application.service.MedicalRecordService;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }
    
    /**
     * Obtiene un historial médico por su ID
     * @param id ID del historial médico
     * @return DTO del historial médico
     */
    @GetMapping("/{id}")
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
    @PostMapping("/{id}/entries")
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
    @DeleteMapping("/{id}/entries/{entryId}")
    public ResponseEntity<MedicalRecordDTO> removeMedicalEntry(
            @PathVariable String id,
            @PathVariable String entryId) {
        
        MedicalRecordDTO updated = medicalRecordService.removeMedicalEntry(id, entryId);
        return ResponseEntity.ok(updated);
    }
} 