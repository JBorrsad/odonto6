package odoonto.api.application;

import odoonto.api.domain.models.Appointment;
import odoonto.api.domain.repositories.AppointmentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import odoonto.api.domain.core.valueobjects.AppointmentStatus;

import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class BookAppointmentService {

    private final AppointmentRepository repo;

    public BookAppointmentService(AppointmentRepository repo) {
        this.repo = repo;
    }

    /**
     * Reserva una cita, comprobando que no haya solapamientos.
     * Devuelve la cita creada.
     */
    public Mono<Appointment> book(
            String patientId,
            String doctorId,
            LocalDateTime start,
            int slots
    ) {
        LocalDateTime end = start.plusMinutes(slots * 30L);
        
        // Convertir a Instant para la consulta
        Instant startInstant = start.atZone(ZoneId.systemDefault()).toInstant();
        Instant endInstant = end.atZone(ZoneId.systemDefault()).toInstant();
        
        return repo
                .findByDoctorIdAndStartGreaterThanEqualAndStartLessThanEqual(doctorId, startInstant.toString(), endInstant.toString())
                .collectList()
                .flatMap(existing -> {
                    boolean overlap = existing.stream().anyMatch(a ->
                            a.end().isAfter(startInstant) &&
                                    Instant.parse(a.getStart()).isBefore(endInstant)
                    );
                    if (overlap) {
                        return Mono.error(new IllegalStateException("Horario ocupado para este doctor"));
                    }
                    // crea y guarda la cita usando el builder
                    Appointment appt = Appointment.builder()
                            .id(UUID.randomUUID().toString())
                            .patientId(patientId)
                            .doctorId(doctorId)
                            .start(startInstant.toString())
                            .durationSlots(slots)
                            .status(AppointmentStatus.PENDING)
                            .build();
                    return repo.save(appt);
                });
    }
}
