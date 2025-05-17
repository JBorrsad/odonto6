package odoonto.infrastructure.persistence.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import java.util.List;
import java.util.ArrayList;

/**
 * Entidad de persistencia para Historiales Médicos en Firestore
 */
public class FirestoreMedicalRecordEntity {
    
    @DocumentId
    private String id;
    
    private String patientId;
    private List<FirestoreMedicalEntryEntity> entries;
    private String fechaCreacion;
    private String fechaActualizacion;
    
    // Constructor vacío necesario para Firestore
    public FirestoreMedicalRecordEntity() {
        this.entries = new ArrayList<>();
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
    
    public List<FirestoreMedicalEntryEntity> getEntries() {
        return entries;
    }
    
    public void setEntries(List<FirestoreMedicalEntryEntity> entries) {
        this.entries = entries;
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
    
    /**
     * Entidad anidada para entradas médicas
     */
    public static class FirestoreMedicalEntryEntity {
        private String date;
        private String description;
        private String doctorId;
        private String type;
        
        // Constructor vacío necesario para Firestore
        public FirestoreMedicalEntryEntity() {}
        
        // Getters y Setters
        
        public String getDate() {
            return date;
        }
        
        public void setDate(String date) {
            this.date = date;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        @PropertyName("doctor_id")
        public String getDoctorId() {
            return doctorId;
        }
        
        @PropertyName("doctor_id")
        public void setDoctorId(String doctorId) {
            this.doctorId = doctorId;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
    }
} 