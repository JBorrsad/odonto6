package odoonto.api.application;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import odoonto.api.domain.models.Appointment;
import odoonto.api.domain.repositories.AppointmentRepository;
import odoonto.api.domain.services.AppointmentScheduler;
import odoonto.api.domain.core.valueobjects.AppointmentStatus;
import java.util.UUID;

@Component
public class AppointmentSchedulerImpl implements AppointmentScheduler {

    private final AppointmentRepository repo;

    public AppointmentSchedulerImpl(AppointmentRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Appointment> schedule(String patientId,
                                      String doctorId,
                                      LocalDateTime start,
                                      int durationSlots ) {
        LocalDateTime end = start.plusMinutes(durationSlots  * 30L);
        
        // Convertir LocalDateTime a Instant para la consulta
        Instant startInstant = start.atZone(ZoneId.systemDefault()).toInstant();
        Instant endInstant = end.atZone(ZoneId.systemDefault()).toInstant();
        Instant windowStart = start.minusMinutes(durationSlots * 30L).atZone(ZoneId.systemDefault()).toInstant();

        // Solo buscamos citas de ESTE doctor en ventana potencial de solapamiento
        return repo.findByDoctorIdAndStartGreaterThanEqualAndStartLessThanEqual(
                        doctorId,
                        windowStart.toString(), 
                        endInstant.toString())
                .collectList()
                .flatMap(existing -> {
                    boolean conflict = existing.stream().anyMatch(a ->
                            Instant.parse(a.getStart()).isBefore(endInstant) &&
                                    a.end().isAfter(startInstant)
                    );
                    if (conflict) {
                        return Mono.error(new IllegalStateException(
                                "El doctor ya tiene otra cita en ese horario"));
                    }
                    // Si no hay conflicto, creamos el Appointment con los parámetros correctos
                    Appointment appointment = Appointment.builder()
                            .id(UUID.randomUUID().toString())
                            .patientId(patientId)
                            .doctorId(doctorId)
                            .start(startInstant.toString())
                            .durationSlots(durationSlots)
                            .status(AppointmentStatus.PENDING)
                            .build();
                    return repo.save(appointment);
                });
    }
}
