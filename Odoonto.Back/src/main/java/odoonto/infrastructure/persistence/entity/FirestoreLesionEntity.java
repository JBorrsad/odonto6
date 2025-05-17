package odoonto.infrastructure.persistence.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import java.util.Map;
import java.util.HashMap;

/**
 * Entidad de persistencia para Lesiones dentales en Firestore
 */
public class FirestoreLesionEntity {
    
    @DocumentId
    private String id;
    
    private String toothId;
    private String face;
    private String lesionType;
    private String fechaDeteccion;
    private Map<String, Object> tratamientoRef;
    
    // Constructor vacío necesario para Firestore
    public FirestoreLesionEntity() {
        this.tratamientoRef = new HashMap<>();
    }
    
    // Getters y Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    @PropertyName("tooth_id")
    public String getToothId() {
        return toothId;
    }
    
    @PropertyName("tooth_id")
    public void setToothId(String toothId) {
        this.toothId = toothId;
    }
    
    public String getFace() {
        return face;
    }
    
    public void setFace(String face) {
        this.face = face;
    }
    
    @PropertyName("lesion_type")
    public String getLesionType() {
        return lesionType;
    }
    
    @PropertyName("lesion_type")
    public void setLesionType(String lesionType) {
        this.lesionType = lesionType;
    }
    
    @PropertyName("fecha_deteccion")
    public String getFechaDeteccion() {
        return fechaDeteccion;
    }
    
    @PropertyName("fecha_deteccion")
    public void setFechaDeteccion(String fechaDeteccion) {
        this.fechaDeteccion = fechaDeteccion;
    }
    
    @PropertyName("tratamiento_ref")
    public Map<String, Object> getTratamientoRef() {
        return tratamientoRef;
    }
    
    @PropertyName("tratamiento_ref")
    public void setTratamientoRef(Map<String, Object> tratamientoRef) {
        this.tratamientoRef = tratamientoRef;
    }
} 