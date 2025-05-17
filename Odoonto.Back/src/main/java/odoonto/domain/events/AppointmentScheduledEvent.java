package odoonto.domain.events;

import java.time.LocalDateTime;

/**
 * Evento que se dispara cuando se programa una nueva cita.
 */
public class AppointmentScheduledEvent extends DomainEvent {
    private final String appointmentId;
    private final String patientId;
    private final String doctorId;
    private final LocalDateTime dateTime;
    private final int duration;
    
    public AppointmentScheduledEvent(String appointmentId, String patientId, 
                                    String doctorId, LocalDateTime dateTime, 
                                    int duration) {
        super();
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.dateTime = dateTime;
        this.duration = duration;
    }
    
    public String getAppointmentId() {
        return appointmentId;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public String getDoctorId() {
        return doctorId;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public int getDuration() {
        return duration;
    }
    
    @Override
    public String toString() {
        return "AppointmentScheduledEvent{" +
                "appointmentId='" + appointmentId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", dateTime=" + dateTime +
                ", duration=" + duration +
                '}';
    }
} 