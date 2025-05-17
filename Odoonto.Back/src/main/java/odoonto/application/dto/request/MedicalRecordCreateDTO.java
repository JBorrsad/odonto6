package odoonto.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para solicitar la creación de un historial médico
 */
public class MedicalRecordCreateDTO {
    
    @NotBlank(message = "El ID del paciente es obligatorio")
    private String patientId;
    
    private List<String> allergies;
    
    private List<String> medicalConditions;
    
    @Valid
    private List<MedicalEntryCreateDTO> entries;
    
    // Constructores
    public MedicalRecordCreateDTO() {
        this.allergies = new ArrayList<>();
        this.medicalConditions = new ArrayList<>();
        this.entries = new ArrayList<>();
    }
    
    public MedicalRecordCreateDTO(String patientId) {
        this();
        this.patientId = patientId;
    }
    
    public MedicalRecordCreateDTO(String patientId, List<String> allergies, 
                                List<String> medicalConditions) {
        this(patientId);
        if (allergies != null) {
            this.allergies = allergies;
        }
        if (medicalConditions != null) {
            this.medicalConditions = medicalConditions;
        }
    }
    
    // Getters y setters
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public List<String> getAllergies() {
        return allergies;
    }
    
    public void setAllergies(List<String> allergies) {
        this.allergies = allergies != null ? allergies : new ArrayList<>();
    }
    
    public List<String> getMedicalConditions() {
        return medicalConditions;
    }
    
    public void setMedicalConditions(List<String> medicalConditions) {
        this.medicalConditions = medicalConditions != null ? medicalConditions : new ArrayList<>();
    }
    
    public List<MedicalEntryCreateDTO> getEntries() {
        return entries;
    }
    
    public void setEntries(List<MedicalEntryCreateDTO> entries) {
        this.entries = entries != null ? entries : new ArrayList<>();
    }
    
    /**
     * DTO para solicitar la creación de una entrada en el historial médico
     */
    public static class MedicalEntryCreateDTO {
        
        @NotBlank(message = "El tipo de entrada médica es obligatorio")
        private String type;
        
        @NotBlank(message = "La descripción de la entrada médica es obligatoria")
        private String description;
        
        private String doctorId;
        
        @NotNull(message = "La fecha de la entrada médica es obligatoria")
        private LocalDate date;
        
        private String notes;
        
        public MedicalEntryCreateDTO() {
        }
        
        public MedicalEntryCreateDTO(String type, String description, String doctorId, 
                                    LocalDate date, String notes) {
            this.type = type;
            this.description = description;
            this.doctorId = doctorId;
            this.date = date;
            this.notes = notes;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getDoctorId() {
            return doctorId;
        }
        
        public void setDoctorId(String doctorId) {
            this.doctorId = doctorId;
        }
        
        public LocalDate getDate() {
            return date;
        }
        
        public void setDate(LocalDate date) {
            this.date = date;
        }
        
        public String getNotes() {
            return notes;
        }
        
        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
} 