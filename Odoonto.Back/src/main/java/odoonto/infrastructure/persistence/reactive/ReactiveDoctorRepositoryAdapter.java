package odoonto.infrastructure.persistence.reactive;

import odoonto.application.port.out.ReactiveDoctorRepository;
import odoonto.domain.model.aggregates.Doctor;
import odoonto.domain.model.valueobjects.Specialty;
import odoonto.domain.repository.DoctorRepository;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Adaptador que implementa la interfaz reactiva para el repositorio de doctores.
 * Actúa como puente entre la interfaz de dominio (síncrona) y la interfaz reactiva
 * utilizada por la capa de aplicación.
 */
@Component
public class ReactiveDoctorRepositoryAdapter implements ReactiveDoctorRepository {

    private final DoctorRepository doctorRepository;

    /**
     * Constructor que recibe el repositorio de dominio
     * @param doctorRepository Repositorio de dominio (síncrono)
     */
    public ReactiveDoctorRepositoryAdapter(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }
    
    @Override
    public Mono<Doctor> findById(String id) {
        return Mono.fromCallable(() -> doctorRepository.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()))
                .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Mono<Doctor> save(Doctor doctor) {
        return Mono.fromCallable(() -> doctorRepository.save(doctor))
                .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.fromRunnable(() -> doctorRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
    
    @Override
    public Flux<Doctor> findAll() {
        return Mono.fromCallable(() -> doctorRepository.findAll())
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Flux<Doctor> findByEspecialidad(Specialty especialidad) {
        return Mono.fromCallable(() -> doctorRepository.findByEspecialidad(especialidad))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Flux<Doctor> findByNombreCompletoContaining(String nombre) {
        return Mono.fromCallable(() -> doctorRepository.findByNombreCompletoContaining(nombre))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }
} 