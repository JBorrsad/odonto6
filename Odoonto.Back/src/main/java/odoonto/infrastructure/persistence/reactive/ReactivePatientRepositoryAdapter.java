package odoonto.infrastructure.persistence.reactive;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.api.core.ApiFuture;

import odoonto.application.port.out.ReactivePatientRepository;
import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.model.valueobjects.PatientId;
import odoonto.infrastructure.persistence.entity.FirestorePatientEntity;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CompletableFuture;

/**
 * Implementación reactiva del repositorio de pacientes usando Firestore
 */
@Component
public class ReactivePatientRepositoryAdapter implements ReactivePatientRepository {

    private static final String COLLECTION_NAME = "patients";
    
    private final Firestore firestore;
    private final CollectionReference patientsCollection;

    /**
     * Constructor
     * @param firestore Cliente Firestore
     */
    public ReactivePatientRepositoryAdapter(Firestore firestore) {
        this.firestore = firestore;
        this.patientsCollection = firestore.collection(COLLECTION_NAME);
    }

    @Override
    public Mono<Patient> findById(String id) {
        if (id == null) {
            return Mono.empty();
        }
        
        return Mono.fromCallable(() -> {
            ApiFuture<DocumentSnapshot> future = patientsCollection.document(id).get();
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
        .filter(DocumentSnapshot::exists)
        .map(this::mapToPatient)
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Mono<Patient> findById(PatientId patientId) {
        if (patientId == null) {
            return Mono.empty();
        }
        return findById(patientId.getValue());
    }

    @Override
    public Mono<Patient> save(Patient patient) {
        if (patient == null) {
            return Mono.error(new IllegalArgumentException("El paciente no puede ser nulo"));
        }
        
        // Asegurar que el paciente tenga un ID
        if (patient.getId() == null) {
            patient.setId(PatientId.generate());
        }
        
        FirestorePatientEntity entity = mapToEntity(patient);
        
        return Mono.fromCallable(() -> {
            ApiFuture<?> future = patientsCollection.document(entity.getId()).set(entity);
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            
            future.addListener(() -> {
                try {
                    completableFuture.complete(future.get());
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
            }, Runnable::run);
            
            return completableFuture.thenApply(result -> patient);
        })
        .flatMap(future -> Mono.fromFuture(future))
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteById(String id) {
        if (id == null) {
            return Mono.empty();
        }
        
        return Mono.fromCallable(() -> {
            ApiFuture<?> future = patientsCollection.document(id).delete();
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
    public Flux<Patient> findAll() {
        return Mono.fromCallable(() -> {
            ApiFuture<QuerySnapshot> future = patientsCollection.get();
            CompletableFuture<QuerySnapshot> completableFuture = new CompletableFuture<>();
            
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
            return Flux.fromIterable(querySnapshot.getDocuments())
                .map(this::mapToPatient)
                .filter(patient -> patient != null);
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Patient> findByNameContaining(String name) {
        // Firestore no soporta búsquedas parciales nativas, 
        // esta es una implementación simple que podría no ser eficiente.
        return findAll()
                .filter(patient -> 
                    (patient.getNombre() != null && 
                     patient.getNombre().toLowerCase().contains(name.toLowerCase())) ||
                    (patient.getApellido() != null && 
                     patient.getApellido().toLowerCase().contains(name.toLowerCase()))
                );
    }

    @Override
    public Flux<Patient> findByPhoneContaining(String phone) {
        // Firestore no soporta búsquedas parciales nativas,
        // esta es una implementación simple que podría no ser eficiente.
        return findAll()
                .filter(patient -> 
                    patient.getTelefono() != null && 
                    patient.getTelefono().getValue().contains(phone)
                );
    }
    
    @Override
    public Flux<Patient> findByAddressContaining(String address) {
        // Implementación simplificada - asumimos que no hay campo de dirección
        // o que se buscaría en otros campos
        return Flux.empty();
    }
    
    @Override
    public Mono<Boolean> existsById(String id) {
        if (id == null) {
            return Mono.just(false);
        }
        
        return Mono.fromCallable(() -> {
            ApiFuture<DocumentSnapshot> future = patientsCollection.document(id).get();
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
        .map(DocumentSnapshot::exists)
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    /**
     * Mapea un documento de Firestore a una entidad de dominio Patient
     */
    private Patient mapToPatient(DocumentSnapshot document) {
        FirestorePatientEntity entity = document.toObject(FirestorePatientEntity.class);
        if (entity == null) {
            return null;
        }
        
        // Implementar la conversión de FirestorePatientEntity a Patient
        Patient patient = new Patient();
        patient.setId(PatientId.of(entity.getId()));
        patient.setNombre(entity.getNombre());
        patient.setApellido(entity.getApellido());
        // ... mapear otras propiedades
        
        return patient;
    }
    
    /**
     * Mapea una entidad de dominio Patient a una entidad de persistencia FirestorePatientEntity
     */
    private FirestorePatientEntity mapToEntity(Patient patient) {
        FirestorePatientEntity entity = new FirestorePatientEntity();
        entity.setId(patient.getIdValue());
        entity.setNombre(patient.getNombre());
        entity.setApellido(patient.getApellido());
        // ... mapear otras propiedades
        
        return entity;
    }
} 