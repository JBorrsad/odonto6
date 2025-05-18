package odoonto.infrastructure.persistence.reactive;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import odoonto.application.port.out.ReactivePatientRepository;
import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.model.valueobjects.PatientId;
import odoonto.infrastructure.persistence.entity.FirestorePatientEntity;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
    public Mono<Patient> findById(PatientId id) {
        if (id == null) {
            return Mono.empty();
        }
        
        return Mono.fromCallable(() -> patientsCollection.document(id.getValue()).get())
                .flatMap(future -> Mono.fromCallable(future::get))
                .filter(DocumentSnapshot::exists)
                .map(this::mapToPatient)
                .subscribeOn(Schedulers.boundedElastic());
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
        
        return Mono.fromCallable(() -> patientsCollection.document(entity.getId()).set(entity))
                .flatMap(future -> Mono.fromCallable(future::get))
                .thenReturn(patient)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteById(PatientId id) {
        if (id == null) {
            return Mono.empty();
        }
        
        return Mono.fromCallable(() -> patientsCollection.document(id.getValue()).delete())
                .flatMap(future -> Mono.fromCallable(future::get))
                .then()
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Patient> findAll() {
        return Mono.fromCallable(() -> patientsCollection.get())
                .flatMap(future -> Mono.fromCallable(future::get))
                .flatMapMany(querySnapshot -> Flux.fromIterable(querySnapshot.getDocuments()))
                .map(this::mapToPatient)
                .filter(patient -> patient != null)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Patient> findByNombreContainingOrApellidoContaining(String nombre, String apellido) {
        // Firestore no soporta búsquedas parciales nativas, 
        // esta es una implementación simple que podría no ser eficiente.
        return findAll()
                .filter(patient -> 
                    (patient.getNombre() != null && 
                     patient.getNombre().toLowerCase().contains(nombre.toLowerCase())) ||
                    (patient.getApellido() != null && 
                     patient.getApellido().toLowerCase().contains(apellido.toLowerCase()))
                );
    }

    @Override
    public Mono<Patient> findByEmail(String email) {
        return Mono.fromCallable(() -> patientsCollection
                    .whereEqualTo("email.value", email)
                    .get())
                .flatMap(future -> Mono.fromCallable(future::get))
                .filter(querySnapshot -> !querySnapshot.isEmpty())
                .map(querySnapshot -> mapToPatient(querySnapshot.getDocuments().get(0)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Patient> findByTelefono(String telefono) {
        return Mono.fromCallable(() -> patientsCollection
                    .whereEqualTo("telefono.value", telefono)
                    .get())
                .flatMap(future -> Mono.fromCallable(future::get))
                .filter(querySnapshot -> !querySnapshot.isEmpty())
                .map(querySnapshot -> mapToPatient(querySnapshot.getDocuments().get(0)))
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