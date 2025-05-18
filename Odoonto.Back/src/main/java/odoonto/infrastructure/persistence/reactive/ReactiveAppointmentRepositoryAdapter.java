package odoonto.infrastructure.persistence.reactive;

import odoonto.application.port.out.ReactiveAppointmentRepository;
import odoonto.domain.model.aggregates.Appointment;
import odoonto.domain.model.valueobjects.AppointmentStatus;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.api.core.ApiFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Adaptador que implementa la interfaz reactiva para el repositorio de citas.
 * Implementa directamente las operaciones reactivas con Firestore.
 */
@Component
public class ReactiveAppointmentRepositoryAdapter implements ReactiveAppointmentRepository {

    private final Firestore firestore;
    private final CollectionReference appointmentsCollection;

    /**
     * Constructor que recibe la instancia de Firestore
     * @param firestore Instancia de Firestore para acceder a la base de datos
     */
    public ReactiveAppointmentRepositoryAdapter(Firestore firestore) {
        this.firestore = firestore;
        this.appointmentsCollection = firestore.collection("appointments");
    }
    
    @Override
    public Mono<Appointment> findById(String id) {
        return Mono.fromCallable(() -> {
            ApiFuture<DocumentSnapshot> future = appointmentsCollection.document(id).get();
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
        .map(this::mapToAppointment)
        .filter(appointment -> appointment != null)
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Mono<Appointment> save(Appointment appointment) {
        return Mono.fromCallable(() -> {
            ApiFuture<?> future = appointmentsCollection.document(appointment.getId().toString()).set(mapToFirestore(appointment));
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            
            future.addListener(() -> {
                try {
                    completableFuture.complete(future.get());
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
            }, Runnable::run);
            
            return completableFuture.thenApply(result -> appointment);
        })
        .flatMap(future -> Mono.fromFuture(future))
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.fromCallable(() -> {
            ApiFuture<?> future = appointmentsCollection.document(id).delete();
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
    public Flux<Appointment> findAll() {
        return Mono.fromCallable(() -> {
            ApiFuture<QuerySnapshot> future = appointmentsCollection.get();
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
            List<Appointment> appointments = new ArrayList<>();
            querySnapshot.getDocuments().forEach(doc -> {
                Appointment appointment = mapToAppointment(doc);
                if (appointment != null) {
                    appointments.add(appointment);
                }
            });
            return Flux.fromIterable(appointments);
        })
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Flux<Appointment> findByPatientId(String patientId) {
        return Mono.fromCallable(() -> {
            ApiFuture<QuerySnapshot> future = appointmentsCollection.whereEqualTo("patientId", patientId).get();
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
        .flatMapMany(querySnapshot -> mapQuerySnapshotToFlux(querySnapshot))
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Flux<Appointment> findByDoctorId(String doctorId) {
        return Mono.fromCallable(() -> {
            ApiFuture<QuerySnapshot> future = appointmentsCollection.whereEqualTo("doctorId", doctorId).get();
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
        .flatMapMany(querySnapshot -> mapQuerySnapshotToFlux(querySnapshot))
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Flux<Appointment> findByDoctorIdAndDateRange(String doctorId, String from, String to) {
        return Mono.fromCallable(() -> {
            ApiFuture<QuerySnapshot> future = appointmentsCollection
                    .whereEqualTo("doctorId", doctorId)
                    .whereGreaterThanOrEqualTo("date", from)
                    .whereLessThanOrEqualTo("date", to)
                    .get();
            
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
        .flatMapMany(querySnapshot -> mapQuerySnapshotToFlux(querySnapshot))
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Flux<Appointment> findByPatientIdAndDateRange(String patientId, String from, String to) {
        return Mono.fromCallable(() -> {
            ApiFuture<QuerySnapshot> future = appointmentsCollection
                    .whereEqualTo("patientId", patientId)
                    .whereGreaterThanOrEqualTo("date", from)
                    .whereLessThanOrEqualTo("date", to)
                    .get();
            
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
        .flatMapMany(querySnapshot -> mapQuerySnapshotToFlux(querySnapshot))
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Flux<Appointment> findByStatus(AppointmentStatus status) {
        return Mono.fromCallable(() -> {
            ApiFuture<QuerySnapshot> future = appointmentsCollection.whereEqualTo("status", status.toString()).get();
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
        .flatMapMany(querySnapshot -> mapQuerySnapshotToFlux(querySnapshot))
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    // Métodos auxiliares para mapeo
    
    private Flux<Appointment> mapQuerySnapshotToFlux(QuerySnapshot querySnapshot) {
        List<Appointment> appointments = new ArrayList<>();
        querySnapshot.getDocuments().forEach(doc -> {
            Appointment appointment = mapToAppointment(doc);
            if (appointment != null) {
                appointments.add(appointment);
            }
        });
        return Flux.fromIterable(appointments);
    }
    
    private Appointment mapToAppointment(DocumentSnapshot document) {
        if (!document.exists()) {
            return null;
        }
        
        // Implementar lógica de mapeo de Firestore a Appointment
        // Este es un ejemplo simplificado, ajustar según la estructura real de datos
        return null; // Reemplazar con implementación real
    }
    
    private Object mapToFirestore(Appointment appointment) {
        // Implementar lógica de mapeo de Appointment a formato Firestore
        // Este es un ejemplo simplificado, ajustar según la estructura real de datos
        return null; // Reemplazar con implementación real
    }
} 