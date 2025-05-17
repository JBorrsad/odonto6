package odoonto.domain.model.entities;

import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.ToothFace;

import java.time.LocalDateTime;

/**
 * Representa una lesión en una cara específica de un diente.
 */
public class Lesion {
    private final ToothFace face;
    private final LesionType type;
    private final LocalDateTime recordedAt;
    private String notes;
    
    /**
     * Constructor para una lesión
     * @param face Cara del diente afectada
     * @param type Tipo de lesión
     */
    public Lesion(ToothFace face, LesionType type) {
        this(face, type, LocalDateTime.now());
    }
    
    /**
     * Constructor para una lesión con tiempo específico
     * @param face Cara del diente afectada
     * @param type Tipo de lesión
     * @param recordedAt Momento en que se registró la lesión
     */
    public Lesion(ToothFace face, LesionType type, LocalDateTime recordedAt) {
        if (face == null) {
            throw new IllegalArgumentException("La cara del diente no puede ser nula");
        }
        if (type == null) {
            throw new IllegalArgumentException("El tipo de lesión no puede ser nulo");
        }
        
        this.face = face;
        this.type = type;
        this.recordedAt = recordedAt;
    }
    
    /**
     * Añade notas adicionales a la lesión
     * @param notes Notas clínicas adicionales
     */
    public void addNotes(String notes) {
        this.notes = notes;
    }
    
    /**
     * Obtiene la cara afectada
     * @return Cara del diente
     */
    public ToothFace getFace() {
        return face;
    }
    
    /**
     * Obtiene el tipo de lesión
     * @return Tipo de lesión
     */
    public LesionType getType() {
        return type;
    }
    
    /**
     * Obtiene el momento en que se registró la lesión
     * @return Fecha y hora de registro
     */
    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }
    
    /**
     * Obtiene las notas asociadas a la lesión
     * @return Notas clínicas o null si no hay notas
     */
    public String getNotes() {
        return notes;
    }
} 