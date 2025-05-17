package odoonto.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO para solicitar la creación de una entrada en el historial médico
 */
public class MedicalEntryCreateDTO {
    
    @NotBlank(message = "El ID del historial médico es obligatorio")
    private String medicalRecordId;
    
    @NotBlank(message = "El tipo de entrada médica es obligatorio")
    private String type;
    
    @NotBlank(message = "La descripción de la entrada médica es obligatoria")
    private String description;
    
    private String doctorId;
    
    @NotNull(message = "La fecha de la entrada médica es obligatoria")
    private LocalDate date;
    
    private String notes;
    
    /**
     * Constructor por defecto
     */
    public MedicalEntryCreateDTO() {
    }
    
    /**
     * Constructor con todos los campos
     */
    public MedicalEntryCreateDTO(String medicalRecordId, String type, String description, String doctorId, 
                                LocalDate date, String notes) {
        this.medicalRecordId = medicalRecordId;
        this.type = type;
        this.description = description;
        this.doctorId = doctorId;
        this.date = date;
        this.notes = notes;
    }
    
    /**
     * Obtiene el ID del historial médico
     */
    public String getMedicalRecordId() {
        return medicalRecordId;
    }
    
    /**
     * Establece el ID del historial médico
     */
    public void setMedicalRecordId(String medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }
    
    /**
     * Obtiene el tipo de entrada médica
     */
    public String getType() {
        return type;
    }
    
    /**
     * Establece el tipo de entrada médica
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * Obtiene la descripción de la entrada médica
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Establece la descripción de la entrada médica
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Obtiene el ID del doctor
     */
    public String getDoctorId() {
        return doctorId;
    }
    
    /**
     * Establece el ID del doctor
     */
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    
    /**
     * Obtiene la fecha de la entrada médica
     */
    public LocalDate getDate() {
        return date;
    }
    
    /**
     * Establece la fecha de la entrada médica
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    /**
     * Obtiene las notas adicionales
     */
    public String getNotes() {
        return notes;
    }
    
    /**
     * Establece las notas adicionales
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
} 