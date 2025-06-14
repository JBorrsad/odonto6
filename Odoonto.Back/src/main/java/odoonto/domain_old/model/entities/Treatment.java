package odoonto.domain.model.entities;

import java.time.LocalDateTime;

/**
 * Representa un tratamiento dental realizado a un diente específico
 */
public class Treatment {
    private final String id;
    private final String description;
    private final LocalDateTime performedAt;
    private final String doctorId;
    private double cost;
    private String notes;
    private boolean completed;
    
    /**
     * Constructor para un tratamiento dental
     * @param id Identificador único del tratamiento
     * @param description Descripción del tratamiento
     * @param doctorId Id del doctor que realizó el tratamiento
     */
    public Treatment(String id, String description, String doctorId) {
        this(id, description, doctorId, LocalDateTime.now());
    }
    
    /**
     * Constructor completo para un tratamiento dental
     * @param id Identificador único del tratamiento
     * @param description Descripción del tratamiento
     * @param doctorId Id del doctor que realizó el tratamiento
     * @param performedAt Fecha y hora en que se realizó
     */
    public Treatment(String id, String description, String doctorId, LocalDateTime performedAt) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del tratamiento no puede estar vacío");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del tratamiento no puede estar vacía");
        }
        if (doctorId == null || doctorId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del doctor no puede estar vacío");
        }
        
        this.id = id;
        this.description = description;
        this.doctorId = doctorId;
        this.performedAt = performedAt;
        this.completed = false;
    }
    
    /**
     * Establece el tratamiento como completado
     */
    public void markAsCompleted() {
        this.completed = true;
    }
    
    /**
     * Establece el tratamiento como no completado
     */
    public void markAsIncomplete() {
        this.completed = false;
    }
    
    /**
     * Añade notas al tratamiento
     * @param notes Notas clínicas
     */
    public void addNotes(String notes) {
        this.notes = notes;
    }
    
    /**
     * Establece el costo del tratamiento
     * @param cost Costo del tratamiento
     */
    public void setCost(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo");
        }
        this.cost = cost;
    }
    
    /**
     * Obtiene el ID del tratamiento
     * @return Identificador único
     */
    public String getId() {
        return id;
    }
    
    /**
     * Obtiene la descripción del tratamiento
     * @return Descripción
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Obtiene la fecha y hora de realización
     * @return Fecha y hora
     */
    public LocalDateTime getPerformedAt() {
        return performedAt;
    }
    
    /**
     * Obtiene el ID del doctor
     * @return ID del doctor
     */
    public String getDoctorId() {
        return doctorId;
    }
    
    /**
     * Obtiene el costo del tratamiento
     * @return Costo
     */
    public double getCost() {
        return cost;
    }
    
    /**
     * Obtiene las notas asociadas
     * @return Notas o null si no hay
     */
    public String getNotes() {
        return notes;
    }
    
    /**
     * Verifica si el tratamiento está completado
     * @return true si está completado
     */
    public boolean isCompleted() {
        return completed;
    }
} 