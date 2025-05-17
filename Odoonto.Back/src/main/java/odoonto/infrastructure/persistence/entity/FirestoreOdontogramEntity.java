package odoonto.infrastructure.persistence.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import java.util.Map;
import java.util.HashMap;

/**
 * Entidad de persistencia para Odontogramas en Firestore
 */
public class FirestoreOdontogramEntity {
    
    @DocumentId
    private String id;
    
    private String patientId;
    private Map<String, Object> dientes;
    private String fechaCreacion;
    private String fechaActualizacion;
    
    // Constructor vacío necesario para Firestore
    public FirestoreOdontogramEntity() {
        this.dientes = new HashMap<>();
    }
    
    // Getters y Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    @PropertyName("patient_id")
    public String getPatientId() {
        return patientId;
    }
    
    @PropertyName("patient_id")
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public Map<String, Object> getDientes() {
        return dientes;
    }
    
    public void setDientes(Map<String, Object> dientes) {
        this.dientes = dientes;
    }
    
    @PropertyName("fecha_creacion")
    public String getFechaCreacion() {
        return fechaCreacion;
    }
    
    @PropertyName("fecha_creacion")
    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    @PropertyName("fecha_actualizacion")
    public String getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    @PropertyName("fecha_actualizacion")
    public void setFechaActualizacion(String fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
} 