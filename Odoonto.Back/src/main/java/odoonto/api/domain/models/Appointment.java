package odoonto.api.domain.models;

import com.google.cloud.spring.data.firestore.Document;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import odoonto.api.domain.core.valueobjects.AppointmentStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import java.util.UUID;

@Document(collectionName = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @DocumentId
    private String id;

    private String patientId;
    private String doctorId;
    private String start; // formato ISO8601: "2023-05-12T14:30:00Z"
    private int durationSlots;
    private AppointmentStatus status;

    // Constructor de negocio: valida y genera su propio ID y estado inicial
    public Appointment(String patientId,
                       String doctorId,
                       LocalDateTime start,
                       int durationSlots) {
        if (start.getMinute() != 0 && start.getMinute() != 30) {
            throw new IllegalArgumentException("La hora debe terminar en :00 o :30");
        }
        if (durationSlots < 1 || durationSlots > 4) {
            throw new IllegalArgumentException("Duración fuera de rango (1-4 bloques)");
        }
        this.id = UUID.randomUUID().toString();
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.start = start.atZone(ZoneId.systemDefault()).toInstant().toString();
        this.durationSlots = durationSlots;
        this.status = AppointmentStatus.PENDING;
    }

    /** Calcula el fin de la cita según bloques de 30 minutos */
    public Instant end() {
        return Instant.parse(this.start).plus(this.durationSlots * 30, ChronoUnit.MINUTES);
    }
    
    /** Convierte el start como Instant a LocalDateTime */
    public LocalDateTime getStartAsLocalDateTime() {
        return LocalDateTime.ofInstant(Instant.parse(this.start), ZoneId.systemDefault());
    }
}
