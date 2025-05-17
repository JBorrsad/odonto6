package odoonto.application.dto.response;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO de respuesta para Odontogram (Odontograma)
 */
public class OdontogramDTO {
    private String id;
    private String patientId;
    private Map<String, ToothRecordDTO> teeth;
    
    // Constructores
    public OdontogramDTO() {
        this.teeth = new HashMap<>();
    }
    
    public OdontogramDTO(String id, String patientId, Map<String, ToothRecordDTO> teeth) {
        this.id = id;
        this.patientId = patientId;
        this.teeth = teeth != null ? teeth : new HashMap<>();
    }
    
    // Getters y setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
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
            this.faces = faces != null ? faces : new HashMap<>();
        }
        
        public Map<String, String> getFaces() {
            return faces;
        }
        
        public void setFaces(Map<String, String> faces) {
            this.faces = faces;
        }
    }
} 