package odoonto.infrastructure.persistence.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import java.util.Map;
import java.util.HashMap;

/**
 * Entidad de persistencia para Pacientes en Firestore
 */
public class FirestorePatientEntity {
    
    @DocumentId
    private String id;
    
    private String nombre;
    private String apellido;
    private String fechaNacimiento;
    private String sexo;
    private String telefono;
    private String email;
    private Map<String, Object> direccion;
    private Map<String, Object> odontogramaRef;
    private Map<String, Object> historialMedicoRef;
    
    // Constructor vacío necesario para Firestore
    public FirestorePatientEntity() {
        this.direccion = new HashMap<>();
        this.odontogramaRef = new HashMap<>();
        this.historialMedicoRef = new HashMap<>();
    }
    
    // Getters y Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public String getFechaNacimiento() {
        return fechaNacimiento;
    }
    
    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    
    public String getSexo() {
        return sexo;
    }
    
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Map<String, Object> getDireccion() {
        return direccion;
    }
    
    public void setDireccion(Map<String, Object> direccion) {
        this.direccion = direccion;
    }
    
    @PropertyName("odontograma_ref")
    public Map<String, Object> getOdontogramaRef() {
        return odontogramaRef;
    }
    
    @PropertyName("odontograma_ref")
    public void setOdontogramaRef(Map<String, Object> odontogramaRef) {
        this.odontogramaRef = odontogramaRef;
    }
    
    @PropertyName("historial_medico_ref")
    public Map<String, Object> getHistorialMedicoRef() {
        return historialMedicoRef;
    }
    
    @PropertyName("historial_medico_ref")
    public void setHistorialMedicoRef(Map<String, Object> historialMedicoRef) {
        this.historialMedicoRef = historialMedicoRef;
    }
} 