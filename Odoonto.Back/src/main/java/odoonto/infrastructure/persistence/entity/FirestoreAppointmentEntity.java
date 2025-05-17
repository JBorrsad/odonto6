package odoonto.infrastructure.persistence.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import java.util.Map;
import java.util.HashMap;

/**
 * Entidad de persistencia para Citas en Firestore
 */
public class FirestoreAppointmentEntity {
    
    @DocumentId
    private String id;
    
    private String patientId;
    private String doctorId;
    private String start;
    private String end;
    private String status;
    private String motivo;
    private String notas;
    private Map<String, Object> tratamientosRealizados;
    
    // Constructor vacío necesario para Firestore
    public FirestoreAppointmentEntity() {
        this.tratamientosRealizados = new HashMap<>();
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
    
    @PropertyName("doctor_id")
    public String getDoctorId() {
        return doctorId;
    }
    
    @PropertyName("doctor_id")
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getStart() {
        return start;
    }
    
    public void setStart(String start) {
        this.start = start;
    }
    
    public String getEnd() {
        return end;
    }
    
    public void setEnd(String end) {
        this.end = end;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMotivo() {
        return motivo;
    }
    
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    
    public String getNotas() {
        return notas;
    }
    
    public void setNotas(String notas) {
        this.notas = notas;
    }
    
    @PropertyName("tratamientos_realizados")
    public Map<String, Object> getTratamientosRealizados() {
        return tratamientosRealizados;
    }
    
    @PropertyName("tratamientos_realizados")
    public void setTratamientosRealizados(Map<String, Object> tratamientosRealizados) {
        this.tratamientosRealizados = tratamientosRealizados;
    }
} 