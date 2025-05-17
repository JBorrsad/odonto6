package odoonto.application.dto.request;

/**
 * DTO para la creación de citas
 */
public class AppointmentCreateDTO {
    private String patientId;
    private String doctorId;
    private String start; // formato ISO8601: "2023-05-12T14:30:00Z"
    private int durationSlots;
    private String notes;
    
    // Constructores
    public AppointmentCreateDTO() {}
    
    public AppointmentCreateDTO(String patientId, String doctorId, String start, int durationSlots, String notes) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.start = start;
        this.durationSlots = durationSlots;
        this.notes = notes;
    }
    
    // Getters y setters
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getStart() {
        return start;
    }
    
    public void setStart(String start) {
        this.start = start;
    }
    
    public int getDurationSlots() {
        return durationSlots;
    }
    
    public void setDurationSlots(int durationSlots) {
        this.durationSlots = durationSlots;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
} 