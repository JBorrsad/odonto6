package odoonto.infrastructure.persistence.reactive;

import odoonto.application.port.out.ReactiveMedicalRecordRepository;
import odoonto.domain.model.entities.MedicalRecord;
import odoonto.domain.model.entities.MedicalEntry;
import odoonto.domain.model.valueobjects.MedicalRecordId;
import odoonto.domain.model.valueobjects.PatientId;
import odoonto.domain.repository.MedicalRecordRepository;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador que implementa la interfaz reactiva para el repositorio de historiales médicos.
 * Actúa como puente entre la interfaz de dominio (síncrona) y la interfaz reactiva
 * utilizada por la capa de aplicación.
 */
@Component
public class ReactiveMedicalRecordRepositoryAdapter implements ReactiveMedicalRecordRepository {

    private final MedicalRecordRepository medicalRecordRepository;

    /**
     * Constructor que recibe el repositorio de dominio
     * @param medicalRecordRepository Repositorio de dominio (síncrono)
     */
    public ReactiveMedicalRecordRepositoryAdapter(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Override
    public Flux<MedicalRecord> findAll() {
        // Como no hay un método findAll directo en MedicalRecordRepository, 
        // combinamos los resultados de findByAllergy y findByMedicalCondition con una cadena vacía
        // para obtener todos los registros
        return Flux.concat(
                Mono.fromCallable(() -> medicalRecordRepository.findByAllergy(""))
                        .flatMapMany(Flux::fromIterable),
                Mono.fromCallable(() -> medicalRecordRepository.findByMedicalCondition(""))
                        .flatMapMany(Flux::fromIterable)
            )
            .distinct(MedicalRecord::getIdValue)
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<MedicalRecord> findById(MedicalRecordId id) {
        return Mono.fromCallable(() -> medicalRecordRepository.findById(id.getValue()))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<MedicalRecord> findByPatientId(PatientId patientId) {
        return Mono.fromCallable(() -> medicalRecordRepository.findByPatientId(patientId.getValue()))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<MedicalRecord> save(MedicalRecord medicalRecord) {
        return Mono.fromCallable(() -> medicalRecordRepository.save(medicalRecord))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteById(MedicalRecordId id) {
        return Mono.fromRunnable(() -> medicalRecordRepository.deleteById(id.getValue()))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Mono<MedicalEntry> addEntry(MedicalRecordId medicalRecordId, MedicalEntry entry) {
        return Mono.fromCallable(() -> medicalRecordRepository.addEntry(medicalRecordId.getValue(), entry))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<MedicalEntry> findAllEntries(MedicalRecordId medicalRecordId) {
        return Mono.fromCallable(() -> medicalRecordRepository.findAllEntries(medicalRecordId.getValue()))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<MedicalEntry> findEntriesByDate(MedicalRecordId medicalRecordId, LocalDate date) {
        return Mono.fromCallable(() -> medicalRecordRepository.findEntriesByDate(medicalRecordId.getValue(), date))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<MedicalEntry> findEntriesByDoctor(MedicalRecordId medicalRecordId, String doctorId) {
        return Mono.fromCallable(() -> medicalRecordRepository.findEntriesByDoctor(medicalRecordId.getValue(), doctorId))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> updateEntry(MedicalRecordId medicalRecordId, String entryId, MedicalEntry entry) {
        return Mono.fromCallable(() -> medicalRecordRepository.updateEntry(medicalRecordId.getValue(), entryId, entry))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> deleteEntry(MedicalRecordId medicalRecordId, String entryId) {
        return Mono.fromCallable(() -> medicalRecordRepository.deleteEntry(medicalRecordId.getValue(), entryId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> addAllergy(MedicalRecordId medicalRecordId, String allergy) {
        return Mono.fromCallable(() -> medicalRecordRepository.addAllergy(medicalRecordId.getValue(), allergy))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> addMedicalCondition(MedicalRecordId medicalRecordId, String condition) {
        return Mono.fromCallable(() -> medicalRecordRepository.addMedicalCondition(medicalRecordId.getValue(), condition))
                .subscribeOn(Schedulers.boundedElastic());
    }
} 