package odoonto.infrastructure.persistence.firestore;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import odoonto.domain.model.aggregates.Appointment;
import odoonto.domain.repository.AppointmentRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Implementación de AppointmentRepository usando Firestore
 */
@Repository
public class FirestoreAppointmentRepository implements AppointmentRepository {

    private static final String COLLECTION_NAME = "appointments";
    
    private final FirestoreTemplate firestoreTemplate;
    private final Firestore firestore;
    
    @Autowired
    public FirestoreAppointmentRepository(FirestoreTemplate firestoreTemplate, Firestore firestore) {
        this.firestoreTemplate = firestoreTemplate;
        this.firestore = firestore;
    }
    
    @Override
    public Mono<Appointment> findById(String id) {
        return firestoreTemplate.findById(id, Appointment.class);
    }
    
    @Override
    public Mono<Appointment> save(Appointment appointment) {
        if (appointment.getId() == null || appointment.getId().trim().isEmpty()) {
            appointment.setId(UUID.randomUUID().toString());
        }
        return firestoreTemplate.save(appointment);
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        return firestoreTemplate.deleteById(id, Appointment.class);
    }
    
    @Override
    public Flux<Appointment> findAll() {
        return firestoreTemplate.findAll(Appointment.class);
    }
    
    @Override
    public Flux<Appointment> findByPatientId(String patientId) {
        return firestoreTemplate.execute(query -> 
                query.collection(COLLECTION_NAME)
                     .whereEqualTo("patientId", patientId))
                .map(snapshot -> firestoreTemplate.getConverter()
                                                .read(Appointment.class, snapshot));
    }
    
    @Override
    public Flux<Appointment> findByDoctorId(String doctorId) {
        return firestoreTemplate.execute(query -> 
                query.collection(COLLECTION_NAME)
                     .whereEqualTo("doctorId", doctorId))
                .map(snapshot -> firestoreTemplate.getConverter()
                                                .read(Appointment.class, snapshot));
    }
    
    @Override
    public Flux<Appointment> findByDoctorIdAndStartGreaterThanEqualAndStartLessThanEqual(
            String doctorId, String from, String to) {
        return firestoreTemplate.execute(query -> 
                query.collection(COLLECTION_NAME)
                     .whereEqualTo("doctorId", doctorId)
                     .whereGreaterThanOrEqualTo("start", from)
                     .whereLessThanOrEqualTo("start", to))
                .map(snapshot -> firestoreTemplate.getConverter()
                                                .read(Appointment.class, snapshot));
    }
    
    @Override
    public Flux<Appointment> findByPatientIdAndStartGreaterThanEqualAndStartLessThanEqual(
            String patientId, String from, String to) {
        return firestoreTemplate.execute(query -> 
                query.collection(COLLECTION_NAME)
                     .whereEqualTo("patientId", patientId)
                     .whereGreaterThanOrEqualTo("start", from)
                     .whereLessThanOrEqualTo("start", to))
                .map(snapshot -> firestoreTemplate.getConverter()
                                                .read(Appointment.class, snapshot));
    }
    
    @Override
    public Flux<Appointment> findByStatus(String status) {
        return firestoreTemplate.execute(query -> 
                query.collection(COLLECTION_NAME)
                     .whereEqualTo("status", status))
                .map(snapshot -> firestoreTemplate.getConverter()
                                                .read(Appointment.class, snapshot));
    }
} 