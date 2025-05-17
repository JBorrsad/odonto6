package odoonto.infrastructure.persistence.firestore;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.model.valueobjects.PatientId;
import odoonto.domain.repository.PatientRepository;
import odoonto.infrastructure.persistence.entity.FirestorePatientEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementación de PatientRepository usando Firestore
 */
@Repository
public class FirestorePatientRepository implements PatientRepository {

    private static final String COLLECTION_NAME = "patients";
    
    private final FirestoreTemplate firestoreTemplate;
    private final Firestore firestore;
    private final CollectionReference patientsCollection;
    
    @Autowired
    public FirestorePatientRepository(FirestoreTemplate firestoreTemplate, Firestore firestore) {
        this.firestoreTemplate = firestoreTemplate;
        this.firestore = firestore;
        this.patientsCollection = firestore.collection(COLLECTION_NAME);
    }
    
    @Override
    public Mono<Patient> findById(PatientId id) {
        if (id == null) {
            return Mono.empty();
        }
        
        return Mono.fromFuture(patientsCollection.document(id.getValue()).get())
                .map(this::mapToPatient)
                .filter(patient -> patient != null);
    }
    
    @Override
    public Mono<Patient> save(Patient patient) {
        if (patient == null) {
            return Mono.empty();
        }
        
        // Asegurar que el paciente tenga un ID
        if (patient.getId() == null) {
            patient.setId(PatientId.generate());
        }
        
        FirestorePatientEntity entity = mapToEntity(patient);
        
        return Mono.fromFuture(patientsCollection.document(entity.getId()).set(entity))
                .thenReturn(patient);
    }
    
    @Override
    public Mono<Void> deleteById(PatientId id) {
        if (id == null) {
            return Mono.empty();
        }
        
        return Mono.fromFuture(patientsCollection.document(id.getValue()).delete())
                .then();
    }
    
    @Override
    public Flux<Patient> findAll() {
        return Flux.from(patientsCollection.get().get().getDocuments().stream())
                .map(this::mapToPatient)
                .filter(patient -> patient != null);
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
        return Flux.from(patientsCollection.whereEqualTo("email.value", email).get().get().getDocuments().stream())
                .map(this::mapToPatient)
                .filter(patient -> patient != null)
                .next();
    }
    
    @Override
    public Mono<Patient> findByTelefono(String telefono) {
        return Flux.from(patientsCollection.whereEqualTo("telefono.value", telefono).get().get().getDocuments().stream())
                .map(this::mapToPatient)
                .filter(patient -> patient != null)
                .next();
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
        // Esto es un ejemplo simplificado
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