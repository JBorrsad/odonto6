package odoonto.infrastructure.persistence.firestore;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.Instant;

import odoonto.domain.model.aggregates.MedicalRecord;
import odoonto.domain.model.entities.MedicalEntry;
import odoonto.domain.repository.MedicalRecordRepository;
import odoonto.infrastructure.persistence.entity.FirestoreMedicalRecordEntity;
import odoonto.infrastructure.persistence.entity.FirestoreMedicalRecordEntity.FirestoreMedicalEntryEntity;

/**
 * Implementación de MedicalRecordRepository para Firestore
 */
@Repository
public class FirestoreMedicalRecordRepository implements MedicalRecordRepository {

    private final Firestore firestore;
    private final CollectionReference medicalRecordsCollection;
    
    @Autowired
    public FirestoreMedicalRecordRepository(Firestore firestore) {
        this.firestore = firestore;
        this.medicalRecordsCollection = firestore.collection("medical_records");
    }
    
    @Override
    public Flux<MedicalRecord> findAll() {
        return Flux.create(sink -> {
            try {
                medicalRecordsCollection.get().get().getDocuments().forEach(doc -> {
                    MedicalRecord record = mapToMedicalRecord(doc);
                    if (record != null) {
                        sink.next(record);
                    }
                });
                sink.complete();
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }
    
    @Override
    public Mono<MedicalRecord> findById(String id) {
        return Mono.fromCallable(() -> 
            medicalRecordsCollection.document(id).get().get())
            .map(this::mapToMedicalRecord);
    }
    
    @Override
    public Mono<MedicalRecord> findByPatientId(String patientId) {
        return Mono.fromCallable(() -> 
            medicalRecordsCollection.whereEqualTo("patient_id", patientId).get().get())
            .flatMap(querySnapshot -> {
                if (querySnapshot.isEmpty()) {
                    return Mono.empty();
                }
                return Mono.just(mapToMedicalRecord(querySnapshot.getDocuments().get(0)));
            });
    }
    
    @Override
    public Mono<MedicalRecord> save(MedicalRecord medicalRecord) {
        FirestoreMedicalRecordEntity entity = mapToEntity(medicalRecord);
        String id = entity.getId();
        
        if (id == null || id.isEmpty()) {
            id = UUID.randomUUID().toString();
            entity.setId(id);
            
            // Fechas de creación y actualización
            String now = Instant.now().toString();
            entity.setFechaCreacion(now);
            entity.setFechaActualizacion(now);
        } else {
            // Solo actualizar la fecha de actualización
            entity.setFechaActualizacion(Instant.now().toString());
        }
        
        return Mono.fromCallable(() -> {
            medicalRecordsCollection.document(id).set(entity).get();
            medicalRecord.setId(id);
            return medicalRecord;
        });
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.fromRunnable(() -> {
            try {
                medicalRecordsCollection.document(id).delete().get();
            } catch (Exception e) {
                throw new RuntimeException("Error deleting medical record", e);
            }
        });
    }
    
    @Override
    public Mono<MedicalRecord> addEntry(String medicalRecordId, MedicalEntry entry) {
        return findById(medicalRecordId)
            .flatMap(record -> {
                record.addEntry(entry);
                return save(record);
            });
    }
    
    /**
     * Mapea un documento de Firestore a una entidad de dominio MedicalRecord
     */
    private MedicalRecord mapToMedicalRecord(DocumentSnapshot document) {
        FirestoreMedicalRecordEntity entity = document.toObject(FirestoreMedicalRecordEntity.class);
        if (entity == null) {
            return null;
        }
        
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setId(entity.getId());
        medicalRecord.setPatientId(entity.getPatientId());
        
        // Mapear entradas médicas
        List<FirestoreMedicalEntryEntity> entries = entity.getEntries();
        if (entries != null) {
            for (FirestoreMedicalEntryEntity entryEntity : entries) {
                MedicalEntry entry = new MedicalEntry();
                entry.setDate(entryEntity.getDate());
                entry.setDescription(entryEntity.getDescription());
                entry.setDoctorId(entryEntity.getDoctorId());
                entry.setType(entryEntity.getType());
                
                medicalRecord.addEntry(entry);
            }
        }
        
        return medicalRecord;
    }
    
    /**
     * Mapea una entidad de dominio MedicalRecord a una entidad de persistencia FirestoreMedicalRecordEntity
     */
    private FirestoreMedicalRecordEntity mapToEntity(MedicalRecord medicalRecord) {
        FirestoreMedicalRecordEntity entity = new FirestoreMedicalRecordEntity();
        entity.setId(medicalRecord.getId());
        entity.setPatientId(medicalRecord.getPatientId());
        
        // Mapear entradas médicas
        List<FirestoreMedicalEntryEntity> entryEntities = new ArrayList<>();
        for (MedicalEntry entry : medicalRecord.getEntries()) {
            FirestoreMedicalEntryEntity entryEntity = new FirestoreMedicalEntryEntity();
            entryEntity.setDate(entry.getDate());
            entryEntity.setDescription(entry.getDescription());
            entryEntity.setDoctorId(entry.getDoctorId());
            entryEntity.setType(entry.getType());
            
            entryEntities.add(entryEntity);
        }
        entity.setEntries(entryEntities);
        
        return entity;
    }
} 