package odoonto.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO para solicitar la creación de un tratamiento en un diente
 */
public class TreatmentCreateDTO {
    @NotBlank(message = "El tipo de tratamiento es obligatorio")
    private String type;
    
    @NotBlank(message = "La descripción del tratamiento es obligatoria")
    private String description;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private String notes;
    
    public TreatmentCreateDTO() {}
    
    public TreatmentCreateDTO(String type, String description, LocalDate startDate, LocalDate endDate, String notes) {
        this.type = type;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
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
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
} 