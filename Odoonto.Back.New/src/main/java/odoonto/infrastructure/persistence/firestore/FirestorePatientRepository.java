package odoonto.infrastructure.persistence.firestore;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.repository.PatientRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Implementación de PatientRepository usando Firestore
 */
@Repository
public class FirestorePatientRepository implements PatientRepository {

    private static final String COLLECTION_NAME = "patients";
    
    private final FirestoreTemplate firestoreTemplate;
    private final Firestore firestore;
    
    @Autowired
    public FirestorePatientRepository(FirestoreTemplate firestoreTemplate, Firestore firestore) {
        this.firestoreTemplate = firestoreTemplate;
        this.firestore = firestore;
    }
    
    @Override
    public Mono<Patient> findById(String id) {
        return firestoreTemplate.findById(id, Patient.class);
    }
    
    @Override
    public Mono<Patient> save(Patient patient) {
        if (patient.getId() == null || patient.getId().trim().isEmpty()) {
            patient.setId(UUID.randomUUID().toString());
        }
        return firestoreTemplate.save(patient);
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        return firestoreTemplate.deleteById(id, Patient.class);
    }
    
    @Override
    public Flux<Patient> findAll() {
        return firestoreTemplate.findAll(Patient.class);
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
        return firestoreTemplate.execute(query -> 
                query.collection(COLLECTION_NAME)
                     .whereEqualTo("email.value", email))
                .next()
                .map(snapshot -> firestoreTemplate.getConverter()
                                                .read(Patient.class, snapshot));
    }
    
    @Override
    public Mono<Patient> findByTelefono(String telefono) {
        return firestoreTemplate.execute(query -> 
                query.collection(COLLECTION_NAME)
                     .whereEqualTo("telefono.value", telefono))
                .next()
                .map(snapshot -> firestoreTemplate.getConverter()
                                                .read(Patient.class, snapshot));
    }
} 