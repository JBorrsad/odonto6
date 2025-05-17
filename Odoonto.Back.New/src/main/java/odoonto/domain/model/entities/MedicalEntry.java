package odoonto.domain.model.entities;

import java.time.LocalDateTime;

/**
 * Representa una entrada individual en el historial médico de un paciente.
 * Puede ser de diferentes tipos: consulta, diagnóstico, procedimiento, etc.
 */
public class MedicalEntry {
    private final String id;
    private final String type;
    private final String description;
    private final String doctorId;
    private final LocalDateTime recordedAt;
    private String notes;
    
    /**
     * Constructor para una entrada médica
     * @param id Identificador único de la entrada
     * @param type Tipo de entrada (CONSULTA, DIAGNOSTICO, PROCEDIMIENTO, etc)
     * @param description Descripción principal de la entrada
     * @param doctorId ID del doctor que realizó la entrada
     */
    public MedicalEntry(String id, String type, String description, String doctorId) {
        this(id, type, description, doctorId, LocalDateTime.now());
    }
    
    /**
     * Constructor completo para una entrada médica
     * @param id Identificador único de la entrada
     * @param type Tipo de entrada (CONSULTA, DIAGNOSTICO, PROCEDIMIENTO, etc)
     * @param description Descripción principal de la entrada
     * @param doctorId ID del doctor que realizó la entrada
     * @param recordedAt Fecha y hora de la entrada
     */
    public MedicalEntry(String id, String type, String description, String doctorId, LocalDateTime recordedAt) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la entrada no puede estar vacío");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de entrada no puede estar vacío");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía");
        }
        if (doctorId == null || doctorId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del doctor no puede estar vacío");
        }
        
        this.id = id;
        this.type = type;
        this.description = description;
        this.doctorId = doctorId;
        this.recordedAt = recordedAt;
    }
    
    /**
     * Añade notas adicionales a la entrada
     * @param notes Notas clínicas adicionales
     */
    public void addNotes(String notes) {
        this.notes = notes;
    }
    
    /**
     * Obtiene el ID de la entrada
     * @return Identificador único
     */
    public String getId() {
        return id;
    }
    
    /**
     * Obtiene el tipo de entrada
     * @return Tipo de entrada
     */
    public String getType() {
        return type;
    }
    
    /**
     * Obtiene la descripción principal
     * @return Descripción
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Obtiene el ID del doctor
     * @return ID del doctor
     */
    public String getDoctorId() {
        return doctorId;
    }
    
    /**
     * Obtiene la fecha y hora de registro
     * @return Fecha y hora
     */
    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }
    
    /**
     * Obtiene las notas adicionales
     * @return Notas o null si no hay
     */
    public String getNotes() {
        return notes;
    }
} 