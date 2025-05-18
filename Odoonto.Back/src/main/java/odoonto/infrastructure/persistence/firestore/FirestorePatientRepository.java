package odoonto.infrastructure.persistence.firestore;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.model.valueobjects.PatientId;
import odoonto.domain.repository.PatientRepository;
import odoonto.infrastructure.persistence.entity.FirestorePatientEntity;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
    public Optional<Patient> findById(PatientId id) {
        if (id == null) {
            return Optional.empty();
        }
        
        try {
            DocumentSnapshot document = patientsCollection.document(id.getValue()).get().get();
            if (document.exists()) {
                return Optional.ofNullable(mapToPatient(document));
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error al buscar paciente: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Patient save(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        
        // Asegurar que el paciente tenga un ID
        if (patient.getId() == null) {
            patient.setId(PatientId.generate());
        }
        
        FirestorePatientEntity entity = mapToEntity(patient);
        
        try {
            patientsCollection.document(entity.getId()).set(entity).get();
            return patient;
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error al guardar paciente: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteById(PatientId id) {
        if (id == null) {
            return;
        }
        
        try {
            patientsCollection.document(id.getValue()).delete().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error al eliminar paciente: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Patient> findAll() {
        try {
            return patientsCollection.get().get().getDocuments().stream()
                    .map(this::mapToPatient)
                    .filter(patient -> patient != null)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error al buscar todos los pacientes: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Patient> findByNombreContainingOrApellidoContaining(String nombre, String apellido) {
        // Firestore no soporta búsquedas parciales nativas, 
        // esta es una implementación simple que podría no ser eficiente.
        return findAll().stream()
                .filter(patient -> 
                    (patient.getNombre() != null && 
                     patient.getNombre().toLowerCase().contains(nombre.toLowerCase())) ||
                    (patient.getApellido() != null && 
                     patient.getApellido().toLowerCase().contains(apellido.toLowerCase()))
                )
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Patient> findByEmail(String email) {
        try {
            QuerySnapshot querySnapshot = patientsCollection
                    .whereEqualTo("email.value", email)
                    .get().get();
            
            if (querySnapshot.isEmpty()) {
                return Optional.empty();
            }
            
            return Optional.ofNullable(mapToPatient(querySnapshot.getDocuments().get(0)));
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error al buscar paciente por email: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<Patient> findByTelefono(String telefono) {
        try {
            QuerySnapshot querySnapshot = patientsCollection
                    .whereEqualTo("telefono.value", telefono)
                    .get().get();
            
            if (querySnapshot.isEmpty()) {
                return Optional.empty();
            }
            
            return Optional.ofNullable(mapToPatient(querySnapshot.getDocuments().get(0)));
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error al buscar paciente por teléfono: " + e.getMessage(), e);
        }
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