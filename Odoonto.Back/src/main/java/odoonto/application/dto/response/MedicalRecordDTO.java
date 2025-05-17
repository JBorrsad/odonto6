package odoonto.application.dto.response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO de respuesta para MedicalRecord (Historial Médico)
 */
public class MedicalRecordDTO {
    private String id;
    private String patientId;
    private List<String> allergies;
    private List<String> medicalConditions;
    private List<MedicalEntryDTO> entries;
    private LocalDate lastUpdated;
    
    // Constructores
    public MedicalRecordDTO() {
        this.allergies = new ArrayList<>();
        this.medicalConditions = new ArrayList<>();
        this.entries = new ArrayList<>();
    }
    
    public MedicalRecordDTO(String id, String patientId, List<String> allergies, 
                           List<String> medicalConditions, List<MedicalEntryDTO> entries,
                           LocalDate lastUpdated) {
        this.id = id;
        this.patientId = patientId;
        this.allergies = allergies != null ? allergies : new ArrayList<>();
        this.medicalConditions = medicalConditions != null ? medicalConditions : new ArrayList<>();
        this.entries = entries != null ? entries : new ArrayList<>();
        this.lastUpdated = lastUpdated;
    }
    
    // Getters y setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
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
        this.allergies = allergies;
    }
    
    public List<String> getMedicalConditions() {
        return medicalConditions;
    }
    
    public void setMedicalConditions(List<String> medicalConditions) {
        this.medicalConditions = medicalConditions;
    }
    
    public List<MedicalEntryDTO> getEntries() {
        return entries;
    }
    
    public void setEntries(List<MedicalEntryDTO> entries) {
        this.entries = entries;
    }
    
    public LocalDate getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    /**
     * DTO para una entrada en el historial médico
     */
    public static class MedicalEntryDTO {
        private String id;
        private String type;
        private String description;
        private String doctorId;
        private LocalDate date;
        private String notes;
        
        public MedicalEntryDTO() {
        }
        
        public MedicalEntryDTO(String id, String type, String description, String doctorId, 
                              LocalDate date, String notes) {
            this.id = id;
            this.type = type;
            this.description = description;
            this.doctorId = doctorId;
            this.date = date;
            this.notes = notes;
        }
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
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