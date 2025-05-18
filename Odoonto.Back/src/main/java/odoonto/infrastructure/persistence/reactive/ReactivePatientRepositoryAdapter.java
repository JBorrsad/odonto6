package odoonto.infrastructure.persistence.reactive;

import odoonto.application.port.out.ReactivePatientRepository;
import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.model.valueobjects.PatientId;
import odoonto.domain.repository.PatientRepository;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Adaptador que implementa la interfaz reactiva para el repositorio de pacientes.
 * Actúa como puente entre la interfaz de dominio (síncrona) y la interfaz reactiva
 * utilizada por la capa de aplicación.
 */
@Component
public class ReactivePatientRepositoryAdapter implements ReactivePatientRepository {

    private final PatientRepository patientRepository;

    /**
     * Constructor que recibe el repositorio de dominio
     * @param patientRepository Repositorio de dominio (síncrono)
     */
    public ReactivePatientRepositoryAdapter(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Mono<Patient> findById(PatientId id) {
        return Mono.fromCallable(() -> patientRepository.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Patient> save(Patient patient) {
        return Mono.fromCallable(() -> patientRepository.save(patient))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteById(PatientId id) {
        return Mono.fromRunnable(() -> patientRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Flux<Patient> findAll() {
        return Mono.fromCallable(() -> patientRepository.findAll())
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Patient> findByNombreContainingOrApellidoContaining(String nombre, String apellido) {
        return Mono.fromCallable(() -> patientRepository.findByNombreContainingOrApellidoContaining(nombre, apellido))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Patient> findByEmail(String email) {
        return Mono.fromCallable(() -> patientRepository.findByEmail(email))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Patient> findByTelefono(String telefono) {
        return Mono.fromCallable(() -> patientRepository.findByTelefono(telefono))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()))
                .subscribeOn(Schedulers.boundedElastic());
    }
} 