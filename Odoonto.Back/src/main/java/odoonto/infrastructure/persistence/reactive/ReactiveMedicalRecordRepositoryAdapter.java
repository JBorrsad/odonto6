package odoonto.infrastructure.persistence.reactive;

import odoonto.application.port.out.ReactiveMedicalRecordRepository;
import odoonto.domain.model.aggregates.MedicalRecord;
import odoonto.domain.model.entities.MedicalEntry;
import odoonto.domain.model.valueobjects.MedicalRecordId;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.FieldValue;
import com.google.api.core.ApiFuture;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Adaptador que implementa la interfaz reactiva para el repositorio de historiales médicos.
 * Implementa directamente las operaciones reactivas con Firestore.
 */
@Component
public class ReactiveMedicalRecordRepositoryAdapter implements ReactiveMedicalRecordRepository {

    private final CollectionReference medicalRecordsCollection;
    
    /**
     * Constructor que recibe la instancia de Firestore
     * @param firestore Instancia de Firestore para acceder a la base de datos
     */
    public ReactiveMedicalRecordRepositoryAdapter(Firestore firestore) {
        this.medicalRecordsCollection = firestore.collection("medical_records");
    }

    @Override
    public Flux<MedicalRecord> findAll() {
        return Mono.fromCallable(() -> {
            ApiFuture<com.google.cloud.firestore.QuerySnapshot> future = medicalRecordsCollection.get();
            CompletableFuture<com.google.cloud.firestore.QuerySnapshot> completableFuture = 
                new CompletableFuture<>();
            
            future.addListener(() -> {
                try {
                    completableFuture.complete(future.get());
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
            }, Runnable::run);
            
            return completableFuture;
        })
        .flatMap(future -> Mono.fromFuture(future))
        .flatMapMany(querySnapshot -> {
            List<MedicalRecord> records = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                MedicalRecord record = mapToMedicalRecord(doc);
                if (record != null) {
                    records.add(record);
                }
            }
            return Flux.fromIterable(records);
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<MedicalRecord> findById(UUID id) {
        String documentId = id.toString();
        return Mono.fromCallable(() -> {
            ApiFuture<DocumentSnapshot> future = medicalRecordsCollection.document(documentId).get();
            CompletableFuture<DocumentSnapshot> completableFuture = new CompletableFuture<>();
            
            future.addListener(() -> {
                try {
                    completableFuture.complete(future.get());
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
            }, Runnable::run);
            
            return completableFuture;
        })
        .flatMap(future -> Mono.fromFuture(future))
        .map(this::mapToMedicalRecord)
        .filter(record -> record != null)
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<MedicalRecord> findByPatientId(UUID patientId) {
        String medicalRecordId = "medical_record_" + patientId.toString();
        return findById(UUID.fromString(medicalRecordId));
    }

    @Override
    public Mono<MedicalRecord> save(MedicalRecord medicalRecord) {
        return Mono.fromCallable(() -> {
            Map<String, Object> data = mapToFirestore(medicalRecord);
            String documentId = medicalRecord.getId().toString();
            
            ApiFuture<?> future = medicalRecordsCollection.document(documentId).set(data);
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            
            future.addListener(() -> {
                try {
                    completableFuture.complete(future.get());
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
            }, Runnable::run);
            
            return completableFuture.thenApply(result -> medicalRecord);
        })
        .flatMap(future -> Mono.fromFuture(future))
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        String documentId = id.toString();
        return Mono.fromCallable(() -> {
            ApiFuture<?> future = medicalRecordsCollection.document(documentId).delete();
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            
            future.addListener(() -> {
                try {
                    completableFuture.complete(future.get());
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
            }, Runnable::run);
            
            return completableFuture;
        })
        .flatMap(future -> Mono.fromFuture(future))
        .then()
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<MedicalEntry> addEntry(MedicalRecordId medicalRecordId, MedicalEntry entry) {
        String documentId = medicalRecordId.getValue();
        return Mono.fromCallable(() -> {
            Map<String, Object> entryData = mapEntryToFirestore(entry);
            
            // Crear una estructura para actualizar el array de entradas
            Map<String, Object> update = new HashMap<>();
            update.put("entries", FieldValue.arrayUnion(entryData));
            update.put("lastUpdated", LocalDate.now().toString());
            
            ApiFuture<?> future = medicalRecordsCollection.document(documentId)
                .set(update, SetOptions.merge());
            
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            future.addListener(() -> {
                try {
                    completableFuture.complete(future.get());
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
            }, Runnable::run);
            
            return completableFuture.thenApply(result -> entry);
        })
        .flatMap(future -> Mono.fromFuture(future))
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<MedicalEntry> findAllEntries(MedicalRecordId medicalRecordId) {
        return findById(UUID.fromString(medicalRecordId.getValue()))
            .flatMapMany(record -> {
                List<MedicalEntry> allEntries = new ArrayList<>();
                
                // Convertir notas a MedicalEntry
                for (MedicalRecord.MedicalNote note : record.getNotes()) {
                    // Crear una MedicalEntry a partir de una nota
                    MedicalEntry entryFromNote = new MedicalEntry(
                        note.getId().toString(),
                        "NOTA",
                        note.getContent(),
                        note.getDoctorId().toString(),
                        note.getCreatedAt()
                    );
                    allEntries.add(entryFromNote);
                }
                
                // Convertir diagnósticos a MedicalEntry
                for (MedicalRecord.Diagnosis diagnosis : record.getDiagnoses()) {
                    // Crear una MedicalEntry a partir de un diagnóstico
                    MedicalEntry entryFromDiagnosis = new MedicalEntry(
                        diagnosis.getId().toString(),
                        "DIAGNOSTICO",
                        diagnosis.getDescription(),
                        diagnosis.getDoctorId().toString(),
                        diagnosis.getCreatedAt()
                    );
                    allEntries.add(entryFromDiagnosis);
                }
                
                // Convertir tratamientos a MedicalEntry
                for (MedicalRecord.Treatment treatment : record.getTreatments()) {
                    // Crear una MedicalEntry a partir de un tratamiento
                    MedicalEntry entryFromTreatment = new MedicalEntry(
                        treatment.getId().toString(),
                        "TRATAMIENTO",
                        treatment.getDescription(),
                        treatment.getDoctorId().toString(),
                        treatment.getPrescriptionDate()
                    );
                    allEntries.add(entryFromTreatment);
                }
                
                return Flux.fromIterable(allEntries);
            })
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<MedicalEntry> findEntriesByDate(MedicalRecordId medicalRecordId, LocalDate date) {
        return findAllEntries(medicalRecordId)
            .filter(entry -> entry.getRecordedAt().toLocalDate().equals(date))
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<MedicalEntry> findEntriesByDoctor(MedicalRecordId medicalRecordId, String doctorId) {
        return findAllEntries(medicalRecordId)
            .filter(entry -> entry.getDoctorId() != null && 
                    entry.getDoctorId().equals(doctorId))
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> updateEntry(MedicalRecordId medicalRecordId, String entryId, MedicalEntry entry) {
        return findById(UUID.fromString(medicalRecordId.getValue()))
            .flatMap(record -> {
                boolean updated = false;
                
                // Buscar en notas
                List<MedicalRecord.MedicalNote> notes = new ArrayList<>(record.getNotes());
                for (int i = 0; i < notes.size(); i++) {
                    if (notes.get(i).getId().toString().equals(entryId)) {
                        // Actualizar nota existente (crear una nueva con el mismo ID)
                        MedicalRecord.MedicalNote updatedNote = new MedicalRecord.MedicalNote(
                            entry.getDescription(),
                            UUID.fromString(entry.getDoctorId())
                        );
                        // En la vida real, necesitaríamos una manera de establecer el ID
                        // Aquí simplemente eliminamos la antigua y añadimos la nueva
                        notes.remove(i);
                        record.getNotes().clear();
                        record.getNotes().addAll(notes);
                        record.addNote(updatedNote);
                        updated = true;
                        break;
                    }
                }
                
                // Si no se encontró en notas, buscar en diagnósticos
                if (!updated) {
                    List<MedicalRecord.Diagnosis> diagnoses = new ArrayList<>(record.getDiagnoses());
                    for (int i = 0; i < diagnoses.size(); i++) {
                        if (diagnoses.get(i).getId().toString().equals(entryId)) {
                            // Actualizar diagnóstico existente
                            MedicalRecord.Diagnosis updatedDiagnosis = new MedicalRecord.Diagnosis(
                                entry.getDescription(),
                                UUID.fromString(entry.getDoctorId())
                            );
                            diagnoses.remove(i);
                            record.getDiagnoses().clear();
                            record.getDiagnoses().addAll(diagnoses);
                            record.addDiagnosis(updatedDiagnosis);
                            updated = true;
                            break;
                        }
                    }
                }
                
                // Si no se encontró en diagnósticos, buscar en tratamientos
                if (!updated) {
                    List<MedicalRecord.Treatment> treatments = new ArrayList<>(record.getTreatments());
                    for (int i = 0; i < treatments.size(); i++) {
                        if (treatments.get(i).getId().toString().equals(entryId)) {
                            // Actualizar tratamiento existente
                            MedicalRecord.Treatment updatedTreatment = new MedicalRecord.Treatment(
                                entry.getDescription(),
                                UUID.fromString(entry.getDoctorId())
                            );
                            // Conservar estado de completado
                            updatedTreatment.setCompleted(treatments.get(i).isCompleted());
                            treatments.remove(i);
                            record.getTreatments().clear();
                            record.getTreatments().addAll(treatments);
                            record.addTreatment(updatedTreatment);
                            updated = true;
                            break;
                        }
                    }
                }
                
                if (!updated) {
                    return Mono.just(false);
                }
                
                return save(record).map(savedRecord -> true);
            })
            .defaultIfEmpty(false)
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> deleteEntry(MedicalRecordId medicalRecordId, String entryId) {
        return findById(UUID.fromString(medicalRecordId.getValue()))
            .flatMap(record -> {
                boolean deleted = false;
                
                // Intenta eliminar de notas
                List<MedicalRecord.MedicalNote> notes = new ArrayList<>(record.getNotes());
                for (int i = 0; i < notes.size(); i++) {
                    if (notes.get(i).getId().toString().equals(entryId)) {
                        notes.remove(i);
                        record.getNotes().clear();
                        record.getNotes().addAll(notes);
                        deleted = true;
                        break;
                    }
                }
                
                // Si no se encontró en notas, intenta eliminar de diagnósticos
                if (!deleted) {
                    List<MedicalRecord.Diagnosis> diagnoses = new ArrayList<>(record.getDiagnoses());
                    for (int i = 0; i < diagnoses.size(); i++) {
                        if (diagnoses.get(i).getId().toString().equals(entryId)) {
                            diagnoses.remove(i);
                            record.getDiagnoses().clear();
                            record.getDiagnoses().addAll(diagnoses);
                            deleted = true;
                            break;
                        }
                    }
                }
                
                // Si no se encontró en diagnósticos, intenta eliminar de tratamientos
                if (!deleted) {
                    List<MedicalRecord.Treatment> treatments = new ArrayList<>(record.getTreatments());
                    for (int i = 0; i < treatments.size(); i++) {
                        if (treatments.get(i).getId().toString().equals(entryId)) {
                            treatments.remove(i);
                            record.getTreatments().clear();
                            record.getTreatments().addAll(treatments);
                            deleted = true;
                            break;
                        }
                    }
                }
                
                if (!deleted) {
                    return Mono.just(false);
                }
                
                return save(record).map(savedRecord -> true);
            })
            .defaultIfEmpty(false)
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> addAllergy(MedicalRecordId medicalRecordId, String allergy) {
        String documentId = medicalRecordId.getValue();
        return Mono.fromCallable(() -> {
            Map<String, Object> update = new HashMap<>();
            update.put("allergies", FieldValue.arrayUnion(allergy));
            update.put("lastUpdated", LocalDate.now().toString());
            
            ApiFuture<?> future = medicalRecordsCollection.document(documentId)
                .set(update, SetOptions.merge());
            
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            future.addListener(() -> {
                try {
                    completableFuture.complete(future.get());
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
            }, Runnable::run);
            
            return completableFuture.thenApply(result -> true);
        })
        .flatMap(future -> Mono.fromFuture(future))
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> addMedicalCondition(MedicalRecordId medicalRecordId, String condition) {
        String documentId = medicalRecordId.getValue();
        return Mono.fromCallable(() -> {
            Map<String, Object> update = new HashMap<>();
            update.put("medicalConditions", FieldValue.arrayUnion(condition));
            update.put("lastUpdated", LocalDate.now().toString());
            
            ApiFuture<?> future = medicalRecordsCollection.document(documentId)
                .set(update, SetOptions.merge());
            
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            future.addListener(() -> {
                try {
                    completableFuture.complete(future.get());
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
            }, Runnable::run);
            
            return completableFuture.thenApply(result -> true);
        })
        .flatMap(future -> Mono.fromFuture(future))
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    // Métodos auxiliares para mapeo
    
    private MedicalRecord mapToMedicalRecord(DocumentSnapshot document) {
        if (document == null || !document.exists()) {
            return null;
        }
        
        try {
            // Implementar lógica de mapeo de Firestore a MedicalRecord
            // Este es un ejemplo simplificado, ajustar según la estructura real del modelo
            // return new MedicalRecord(...); 
            
            return null; // Reemplazar con implementación real
        } catch (Exception e) {
            return null;
        }
    }
    
    private Map<String, Object> mapToFirestore(MedicalRecord record) {
        Map<String, Object> data = new HashMap<>();
        // Implementar lógica de mapeo de MedicalRecord a formato Firestore
        // Este es un ejemplo simplificado, ajustar según la estructura real del modelo
        
        return data;
    }
    
    private Map<String, Object> mapEntryToFirestore(MedicalEntry entry) {
        Map<String, Object> data = new HashMap<>();
        // Implementar lógica de mapeo de MedicalEntry a formato Firestore
        // Este es un ejemplo simplificado, ajustar según la estructura real del modelo
        
        return data;
    }
} 