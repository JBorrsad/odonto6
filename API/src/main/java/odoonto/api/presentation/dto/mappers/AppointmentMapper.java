package odoonto.api.presentation.dto.mappers;

import org.springframework.stereotype.Component;
import odoonto.api.domain.models.Appointment;
import odoonto.api.presentation.dto.AppointmentDto;
import odoonto.api.presentation.dto.CreateAppointmentDto;
import odoonto.api.domain.core.valueobjects.AppointmentStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Component
public class AppointmentMapper {

    public AppointmentDto toDto(Appointment a) {
        // El start ahora es String
        // Pasamos directamente el String al DTO
        return new AppointmentDto(
                a.getId(),
                a.getPatientId(),
                a.getDoctorId(),
                a.getStart(), // String en formato ISO8601
                // el campo se llama durationSlots en la entidad
                a.getDurationSlots(),
                // end() ahora devuelve un Instant, lo convertimos a String
                a.end().toString() // String en formato ISO8601
        );
    }

    public Appointment toEntity(CreateAppointmentDto dto) {
        // Si el DTO proporciona una fecha como String
        LocalDateTime startDateTime;
        if (dto.getStart() instanceof String) {
            try {
                startDateTime = LocalDateTime.ofInstant(
                        Instant.parse((String) dto.getStart()), 
                        ZoneId.systemDefault()
                );
            } catch (Exception e) {
                throw new IllegalArgumentException("Formato de fecha inválido: " + dto.getStart());
            }
        } else {
            // Intentar convertir de String a LocalDateTime para el constructor
            try {
                startDateTime = LocalDateTime.parse((String) dto.getStart());
            } catch (Exception e) {
                // Si falla, usar fecha actual como fallback
                startDateTime = LocalDateTime.now();
            }
        }
        
        return Appointment.builder()
                .id(UUID.randomUUID().toString())
                .patientId(dto.getPatientId())
                .doctorId(dto.getDoctorId())
                .start(startDateTime.atZone(ZoneId.systemDefault()).toInstant().toString())
                .durationSlots(dto.getSlots())
                .status(AppointmentStatus.PENDING)
                .build();
    }
}
