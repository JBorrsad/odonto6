package odoonto.application.dto.response;

/**
 * DTO de respuesta para Appointment (Cita)
 */
public class AppointmentDTO {
    private String id;
    private String patientId;
    private String patientName; // Opcional: información adicional del paciente
    private String doctorId;
    private String doctorName; // Opcional: información adicional del doctor
    private String start; // formato ISO8601: "2023-05-12T14:30:00Z"
    private String end; // formato ISO8601: "2023-05-12T15:00:00Z"
    private int durationSlots;
    private String status;
    private String notes;
    
    // Constructores
    public AppointmentDTO() {}
    
    public AppointmentDTO(String id, String patientId, String doctorId, String start, 
                         String end, int durationSlots, String status, String notes) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.start = start;
        this.end = end;
        this.durationSlots = durationSlots;
        this.status = status;
        this.notes = notes;
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
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public String getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    public String getStart() {
        return start;
    }
    
    public void setStart(String start) {
        this.start = start;
    }
    
    public String getEnd() {
        return end;
    }
    
    public void setEnd(String end) {
        this.end = end;
    }
    
    public int getDurationSlots() {
        return durationSlots;
    }
    
    public void setDurationSlots(int durationSlots) {
        this.durationSlots = durationSlots;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
} 