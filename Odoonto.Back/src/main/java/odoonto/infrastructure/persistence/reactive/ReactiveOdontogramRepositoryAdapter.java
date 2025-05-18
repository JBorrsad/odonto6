package odoonto.infrastructure.persistence.reactive;

import odoonto.application.port.out.ReactiveOdontogramRepository;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.entities.Tooth;
import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.OdontogramId;
import odoonto.domain.model.valueobjects.PatientId;
import odoonto.domain.repository.OdontogramRepository;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Adaptador que implementa la interfaz reactiva para el repositorio de odontogramas.
 * Actúa como puente entre la interfaz de dominio (síncrona) y la interfaz reactiva
 * utilizada por la capa de aplicación.
 */
@Component
public class ReactiveOdontogramRepositoryAdapter implements ReactiveOdontogramRepository {

    private final OdontogramRepository odontogramRepository;

    /**
     * Constructor que recibe el repositorio de dominio
     * @param odontogramRepository Repositorio de dominio (síncrono)
     */
    public ReactiveOdontogramRepositoryAdapter(OdontogramRepository odontogramRepository) {
        this.odontogramRepository = odontogramRepository;
    }

    @Override
    public Flux<Odontogram> findAll() {
        return Mono.fromCallable(() -> odontogramRepository.findAll())
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Odontogram> findById(OdontogramId id) {
        return Mono.fromCallable(() -> odontogramRepository.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Odontogram> findByPatientId(PatientId patientId) {
        return Mono.fromCallable(() -> odontogramRepository.findByPatientId(patientId))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Odontogram> save(Odontogram odontogram) {
        return Mono.fromCallable(() -> odontogramRepository.save(odontogram))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteById(OdontogramId id) {
        return Mono.fromRunnable(() -> odontogramRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Flux<Odontogram> findByLesionType(LesionType lesionType) {
        return Mono.fromCallable(() -> odontogramRepository.findByLesionType(lesionType))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> existsByPatientId(PatientId patientId) {
        return Mono.fromCallable(() -> odontogramRepository.existsByPatientId(patientId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteByPatientId(PatientId patientId) {
        return Mono.fromRunnable(() -> odontogramRepository.deleteByPatientId(patientId))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Mono<String> createHistoricalCopy(OdontogramId odontogramId) {
        return Mono.fromCallable(() -> odontogramRepository.createHistoricalCopy(odontogramId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Odontogram> findHistoryByPatientId(PatientId patientId) {
        return Mono.fromCallable(() -> odontogramRepository.findHistoryByPatientId(patientId))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Odontogram> findHistoricalByPatientIdAndVersion(PatientId patientId, String version) {
        return Mono.fromCallable(() -> odontogramRepository.findHistoricalByPatientIdAndVersion(patientId, version))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> updateTooth(String patientId, String toothNumber, Tooth tooth) {
        return Mono.fromCallable(() -> odontogramRepository.updateTooth(patientId, toothNumber, tooth))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> removeLesion(String odontogramId, String toothNumber, String lesionId) {
        return Mono.fromRunnable(() -> odontogramRepository.removeLesion(odontogramId, toothNumber, lesionId))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Mono<Void> addTreatment(String odontogramId, String toothNumber, Object treatmentData) {
        return Mono.fromRunnable(() -> odontogramRepository.addTreatment(odontogramId, toothNumber, treatmentData))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Mono<Void> removeTreatment(String odontogramId, String toothNumber, String treatmentId) {
        return Mono.fromRunnable(() -> odontogramRepository.removeTreatment(odontogramId, toothNumber, treatmentId))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
} 