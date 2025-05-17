package odoonto.domain.events;

import odoonto.domain.model.valueobjects.AppointmentStatus;

/**
 * Evento que se dispara cuando cambia el estado de una cita.
 */
public class AppointmentStatusChangedEvent extends DomainEvent {
    private final String appointmentId;
    private final AppointmentStatus previousStatus;
    private final AppointmentStatus newStatus;
    private final String changedBy;
    private final String reason;
    
    public AppointmentStatusChangedEvent(String appointmentId, 
                                        AppointmentStatus previousStatus,
                                        AppointmentStatus newStatus,
                                        String changedBy,
                                        String reason) {
        super();
        this.appointmentId = appointmentId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.reason = reason;
    }
    
    public String getAppointmentId() {
        return appointmentId;
    }
    
    public AppointmentStatus getPreviousStatus() {
        return previousStatus;
    }
    
    public AppointmentStatus getNewStatus() {
        return newStatus;
    }
    
    public String getChangedBy() {
        return changedBy;
    }
    
    public String getReason() {
        return reason;
    }
    
    @Override
    public String toString() {
        return "AppointmentStatusChangedEvent{" +
                "appointmentId='" + appointmentId + '\'' +
                ", previousStatus=" + previousStatus +
                ", newStatus=" + newStatus +
                ", changedBy='" + changedBy + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
} 