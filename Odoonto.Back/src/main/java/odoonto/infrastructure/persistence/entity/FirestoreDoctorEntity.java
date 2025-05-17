package odoonto.infrastructure.persistence.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Entidad de persistencia para Doctores en Firestore
 */
public class FirestoreDoctorEntity {
    
    @DocumentId
    private String id;
    
    private String nombre;
    private String apellido;
    private String especialidad;
    private String telefono;
    private String email;
    private Map<String, Object> direccion;
    private List<String> horarioDisponible;
    
    // Constructor vacío necesario para Firestore
    public FirestoreDoctorEntity() {
        this.direccion = new HashMap<>();
        this.horarioDisponible = new ArrayList<>();
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
    
    public String getEspecialidad() {
        return especialidad;
    }
    
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
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
    
    @PropertyName("horario_disponible")
    public List<String> getHorarioDisponible() {
        return horarioDisponible;
    }
    
    @PropertyName("horario_disponible")
    public void setHorarioDisponible(List<String> horarioDisponible) {
        this.horarioDisponible = horarioDisponible;
    }
} 