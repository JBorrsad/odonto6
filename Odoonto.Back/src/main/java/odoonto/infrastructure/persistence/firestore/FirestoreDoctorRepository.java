package odoonto.infrastructure.persistence.firestore;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import odoonto.domain.model.aggregates.Doctor;
import odoonto.domain.repository.DoctorRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Implementación de DoctorRepository usando Firestore
 */
@Repository
public class FirestoreDoctorRepository implements DoctorRepository {

    private static final String COLLECTION_NAME = "doctors";
    
    private final FirestoreTemplate firestoreTemplate;
    private final Firestore firestore;
    
    @Autowired
    public FirestoreDoctorRepository(FirestoreTemplate firestoreTemplate, Firestore firestore) {
        this.firestoreTemplate = firestoreTemplate;
        this.firestore = firestore;
    }
    
    @Override
    public Mono<Doctor> findById(String id) {
        return firestoreTemplate.findById(id, Doctor.class);
    }
    
    @Override
    public Mono<Doctor> save(Doctor doctor) {
        if (doctor.getId() == null || doctor.getId().trim().isEmpty()) {
            doctor.setId(UUID.randomUUID().toString());
        }
        return firestoreTemplate.save(doctor);
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        return firestoreTemplate.deleteById(id, Doctor.class);
    }
    
    @Override
    public Flux<Doctor> findAll() {
        return firestoreTemplate.findAll(Doctor.class);
    }
    
    @Override
    public Flux<Doctor> findByEspecialidad(String especialidad) {
        return firestoreTemplate.execute(query -> 
                query.collection(COLLECTION_NAME)
                     .whereEqualTo("especialidad", especialidad))
               .map(snapshot -> firestoreTemplate.getConverter()
                                               .read(Doctor.class, snapshot));
    }
    
    @Override
    public Flux<Doctor> findByNombreCompletoContaining(String nombre) {
        // Firestore no soporta búsquedas parciales nativas, 
        // esta es una implementación simple que podría no ser eficiente.
        // En producción, considerar Firebase Search o Algolia.
        return findAll()
                .filter(doctor -> doctor.getNombreCompleto() != null && 
                                 doctor.getNombreCompleto().toLowerCase()
                                       .contains(nombre.toLowerCase()));
    }
} 