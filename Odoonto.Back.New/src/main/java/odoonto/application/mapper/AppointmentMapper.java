package odoonto.application.mapper;

import odoonto.domain.model.aggregates.Appointment;
import odoonto.application.dto.response.AppointmentDTO;
import odoonto.application.dto.request.AppointmentCreateDTO;
import odoonto.domain.model.valueobjects.AppointmentStatus;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Mapper para convertir entre Appointment y sus DTOs
 */
@Component
public class AppointmentMapper {
    
    /**
     * Convierte una entidad Appointment a AppointmentDTO
     */
    public AppointmentDTO toDTO(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setPatientId(appointment.getPatientId());
        dto.setDoctorId(appointment.getDoctorId());
        dto.setStart(appointment.getStart());
        dto.setEnd(appointment.end().toString());
        dto.setDurationSlots(appointment.getDurationSlots());
        dto.setStatus(appointment.getStatus().toString());
        
        return dto;
    }
    
    /**
     * Convierte un AppointmentCreateDTO a entidad Appointment
     */
    public Appointment toEntity(AppointmentCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        
        // Convertir String ISO a LocalDateTime para el constructor del dominio
        LocalDateTime startTime = LocalDateTime.ofInstant(
            Instant.parse(dto.getStart()), 
            ZoneId.systemDefault()
        );
        
        Appointment appointment = new Appointment(
            dto.getPatientId(),
            dto.getDoctorId(),
            startTime,
            dto.getDurationSlots()
        );
        
        return appointment;
    }
    
    /**
     * Actualiza una entidad Appointment existente con datos de un DTO
     * Nota: En este caso es limitado porque muchos campos como status se gestionan en el dominio
     */
    public void updateAppointmentFromDTO(AppointmentCreateDTO dto, Appointment appointment) {
        if (dto == null || appointment == null) {
            return;
        }
        
        // En este caso, debemos crear una nueva instancia ya que Appointment
        // valida sus datos críticos en el constructor
        if (dto.getStart() != null || dto.getDurationSlots() > 0) {
            LocalDateTime startTime = dto.getStart() != null ? 
                LocalDateTime.ofInstant(Instant.parse(dto.getStart()), ZoneId.systemDefault()) :
                appointment.getStartAsLocalDateTime();
                
            int durationSlots = dto.getDurationSlots() > 0 ? 
                dto.getDurationSlots() : appointment.getDurationSlots();
                
            // Solo actualiza si cambió algo
            if (!startTime.equals(appointment.getStartAsLocalDateTime()) || 
                durationSlots != appointment.getDurationSlots()) {
                
                // Nota: En una implementación completa, habría que preservar el id y el status
                appointment.setPatientId(dto.getPatientId() != null ? dto.getPatientId() : appointment.getPatientId());
                appointment.setDoctorId(dto.getDoctorId() != null ? dto.getDoctorId() : appointment.getDoctorId());
                appointment.setStart(startTime.atZone(ZoneId.systemDefault()).toInstant().toString());
                appointment.setDurationSlots(durationSlots);
            }
        }
    }
} 