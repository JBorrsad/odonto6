package odoonto.infrastructure.persistence.reactive;

import odoonto.application.port.out.ReactiveAppointmentRepository;
import odoonto.domain.model.aggregates.Appointment;
import odoonto.domain.model.valueobjects.AppointmentStatus;
import odoonto.domain.repository.AppointmentRepository;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Adaptador que implementa la interfaz reactiva para el repositorio de citas.
 * Actúa como puente entre la interfaz de dominio (síncrona) y la interfaz reactiva
 * utilizada por la capa de aplicación.
 */
@Component
public class ReactiveAppointmentRepositoryAdapter implements ReactiveAppointmentRepository {

    private final AppointmentRepository appointmentRepository;

    /**
     * Constructor que recibe el repositorio de dominio
     * @param appointmentRepository Repositorio de dominio (síncrono)
     */
    public ReactiveAppointmentRepositoryAdapter(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }
    
    @Override
    public Mono<Appointment> findById(String id) {
        return Mono.fromCallable(() -> appointmentRepository.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()))
                .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Mono<Appointment> save(Appointment appointment) {
        return Mono.fromCallable(() -> appointmentRepository.save(appointment))
                .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.fromRunnable(() -> appointmentRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
    
    @Override
    public Flux<Appointment> findAll() {
        return Mono.fromCallable(() -> appointmentRepository.findAll())
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Flux<Appointment> findByPatientId(String patientId) {
        return Mono.fromCallable(() -> appointmentRepository.findByPatientId(patientId))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Flux<Appointment> findByDoctorId(String doctorId) {
        return Mono.fromCallable(() -> appointmentRepository.findByDoctorId(doctorId))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Flux<Appointment> findByDoctorIdAndDateRange(String doctorId, String from, String to) {
        return Mono.fromCallable(() -> appointmentRepository.findByDoctorIdAndDateRange(doctorId, from, to))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Flux<Appointment> findByPatientIdAndDateRange(String patientId, String from, String to) {
        return Mono.fromCallable(() -> appointmentRepository.findByPatientIdAndDateRange(patientId, from, to))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Flux<Appointment> findByStatus(AppointmentStatus status) {
        return Mono.fromCallable(() -> appointmentRepository.findByStatus(status))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }
} 