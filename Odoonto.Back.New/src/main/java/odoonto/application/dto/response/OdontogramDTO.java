package odoonto.application.dto.response;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO de respuesta para Odontogram (Odontograma)
 */
public class OdontogramDTO {
    private Map<String, ToothRecordDTO> teeth;
    
    // Constructores
    public OdontogramDTO() {
        this.teeth = new HashMap<>();
    }
    
    public OdontogramDTO(Map<String, ToothRecordDTO> teeth) {
        this.teeth = teeth;
    }
    
    // Getters y setters
    public Map<String, ToothRecordDTO> getTeeth() {
        return teeth;
    }
    
    public void setTeeth(Map<String, ToothRecordDTO> teeth) {
        this.teeth = teeth;
    }
    
    /**
     * Clase interna para representar el registro de un diente
     */
    public static class ToothRecordDTO {
        private Map<String, String> faces;
        
        public ToothRecordDTO() {
            this.faces = new HashMap<>();
        }
        
        public ToothRecordDTO(Map<String, String> faces) {
            this.faces = faces;
        }
        
        public Map<String, String> getFaces() {
            return faces;
        }
        
        public void setFaces(Map<String, String> faces) {
            this.faces = faces;
        }
    }
} 