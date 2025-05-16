package odoonto.api.application;

import odoonto.api.domain.models.Appointment;
import odoonto.api.domain.repositories.AppointmentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ManageAppointmentsService {

    private final AppointmentRepository repo;

    public ManageAppointmentsService(AppointmentRepository repo) {
        this.repo = repo;
    }

    public Flux<Appointment> findAll() {
        return repo.findAll();
    }

    public Mono<Appointment> findById(String id) {
        return repo.findById(id);
    }

    public Mono<Appointment> create(Appointment appointment) {
        return repo.save(appointment);
    }

    public Mono<Appointment> update(String id, Appointment appointment) {
        appointment.setId(id);
        return repo.save(appointment);
    }

    public Mono<Void> delete(String id) {
        return repo.deleteById(id);
    }
}
