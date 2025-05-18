package odoonto.application.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para la creación de citas
 */
public class AppointmentCreateDTO {
    private String doctorId;
    private String patientId;
    private LocalDate date;
    private LocalTime time;
    private Integer duration;
    private String notes;
    
    // Constructores
    public AppointmentCreateDTO() {}
    
    public AppointmentCreateDTO(String doctorId, String patientId, LocalDate date, LocalTime time, Integer duration, String notes) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.notes = notes;
    }
    
    // Getters y setters
    public String getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public LocalTime getTime() {
        return time;
    }
    
    public void setTime(LocalTime time) {
        this.time = time;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
} 