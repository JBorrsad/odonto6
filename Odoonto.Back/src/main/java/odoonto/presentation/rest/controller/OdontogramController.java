package odoonto.presentation.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import odoonto.application.dto.response.OdontogramDTO;
import odoonto.application.service.OdontogramService;

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
     * Obtiene un odontograma por su ID
     * @param id ID del odontograma
     * @return DTO del odontograma
     */
    @GetMapping("/{id}")
    public ResponseEntity<OdontogramDTO> getOdontogramById(@PathVariable String id) {
        OdontogramDTO odontogram = odontogramService.getOdontogramById(id);
        return ResponseEntity.ok(odontogram);
    }
    
    /**
     * Registra una lesión en un diente
     * @param id ID del odontograma
     * @param toothNumber Número del diente
     * @param face Cara del diente
     * @param lesionType Tipo de lesión
     * @return Odontograma actualizado
     */
    @PostMapping("/{id}/teeth/{toothNumber}/faces/{face}/lesions")
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
    @DeleteMapping("/{id}/teeth/{toothNumber}/faces/{face}/lesions")
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
    @PostMapping("/{id}/teeth/{toothNumber}/treatments")
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
    @DeleteMapping("/{id}/teeth/{toothNumber}/treatments")
    public ResponseEntity<OdontogramDTO> removeTreatment(
            @PathVariable String id,
            @PathVariable int toothNumber) {
        
        OdontogramDTO updated = odontogramService.removeTreatment(id, toothNumber);
        return ResponseEntity.ok(updated);
    }
} 