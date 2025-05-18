package odoonto.application.mapper;

import odoonto.domain.model.aggregates.Appointment;
import odoonto.application.dto.response.AppointmentDTO;
import odoonto.application.dto.request.AppointmentCreateDTO;

import org.springframework.stereotype.Component;

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
        
        // Convertir LocalDateTime a formato ISO
        LocalDateTime dateTime = appointment.getDateTime();
        String startIso = dateTime.atZone(ZoneId.systemDefault()).toInstant().toString();
        dto.setStart(startIso);
        
        // Calcular fecha y hora de fin (30 minutos por slot)
        LocalDateTime endDateTime = dateTime.plusMinutes(appointment.getDurationSlots() * 30);
        String endIso = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toString();
        dto.setEnd(endIso);
        
        dto.setDurationSlots(appointment.getDurationSlots());
        dto.setStatus(appointment.getStatus().toString());
        dto.setNotes(appointment.getNotes());
        
        return dto;
    }
    
    /**
     * Convierte un AppointmentCreateDTO a entidad Appointment
     */
    public Appointment toEntity(AppointmentCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        
        // Combinar fecha y hora en un LocalDateTime
        LocalDateTime dateTime = null;
        if (dto.getDate() != null && dto.getTime() != null) {
            dateTime = LocalDateTime.of(dto.getDate(), dto.getTime());
        } else {
            throw new IllegalArgumentException("La fecha y hora son obligatorias");
        }
        
        // Calcular slots de duración (30 minutos por slot)
        int durationSlots = 1; // Por defecto, 1 slot (30 minutos)
        if (dto.getDuration() != null && dto.getDuration() > 0) {
            durationSlots = (dto.getDuration() + 29) / 30; // Redondear hacia arriba
        }
        
        Appointment appointment = new Appointment(
            dto.getPatientId(),
            dto.getDoctorId(),
            dateTime,
            durationSlots
        );
        
        if (dto.getNotes() != null) {
            appointment.setNotes(dto.getNotes());
        }
        
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
        
        // Actualizar IDs si están presentes
        if (dto.getPatientId() != null && !dto.getPatientId().isEmpty()) {
            appointment.setPatientId(dto.getPatientId());
        }
        
        if (dto.getDoctorId() != null && !dto.getDoctorId().isEmpty()) {
            appointment.setDoctorId(dto.getDoctorId());
        }
        
        // Actualizar fecha/hora si están presentes
        boolean timeChanged = false;
        LocalDateTime newDateTime = appointment.getDateTime();
        
        if (dto.getDate() != null) {
            // Mantener la hora, actualizar solo la fecha
            newDateTime = LocalDateTime.of(
                dto.getDate(), 
                newDateTime.toLocalTime()
            );
            timeChanged = true;
        }
        
        if (dto.getTime() != null) {
            // Mantener la fecha, actualizar solo la hora
            newDateTime = LocalDateTime.of(
                newDateTime.toLocalDate(),
                dto.getTime()
            );
            timeChanged = true;
        }
        
        // Si cambió la fecha u hora, actualizar
        if (timeChanged) {
            appointment.setDateTime(newDateTime);
        }
        
        // Actualizar duración si está presente
        if (dto.getDuration() != null && dto.getDuration() > 0) {
            int durationSlots = (dto.getDuration() + 29) / 30; // Redondear hacia arriba
            appointment.setDurationSlots(durationSlots);
        }
        
        // Actualizar notas si están presentes
        if (dto.getNotes() != null) {
            appointment.setNotes(dto.getNotes());
        }
    }
} 