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
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Adaptador que implementa la interfaz reactiva para el repositorio de citas.
 * Implementa directamente las operaciones reactivas con Firestore.
 */
@Component
public class ReactiveAppointmentRepositoryAdapter implements ReactiveAppointmentRepository {

    private final CollectionReference appointmentsCollection;

    /**
     * Constructor que recibe la instancia de Firestore
     * @param firestore Instancia de Firestore para acceder a la base de datos
     */
    public ReactiveAppointmentRepositoryAdapter(Firestore firestore) {
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
            // Si el ID es nulo, generar un nuevo UUID
            if (appointment.getId() == null || appointment.getId().trim().isEmpty()) {
                appointment.setId(java.util.UUID.randomUUID().toString());
            }
            
            ApiFuture<?> future = appointmentsCollection.document(appointment.getId()).set(mapToFirestore(appointment));
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
        
        try {
            // Extraer datos del documento
            String id = document.getId();
            String patientId = document.getString("patientId");
            String doctorId = document.getString("doctorId");
            String dateTimeStr = document.getString("dateTime");
            Long durationSlotsLong = document.getLong("durationSlots");
            String statusStr = document.getString("status");
            String notes = document.getString("notes");
            
            // Validar datos esenciales
            if (patientId == null || doctorId == null || dateTimeStr == null || durationSlotsLong == null) {
                System.err.println("Error: Documento de cita con datos incompletos - ID: " + id);
                return null;
            }
            
            // Convertir tipos
            int durationSlots = durationSlotsLong.intValue();
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
            AppointmentStatus status = (statusStr != null) ? 
                AppointmentStatus.valueOf(statusStr) : AppointmentStatus.PENDIENTE;
            
            // Crear la cita usando un constructor especial para datos de persistencia
            // que no valide fechas pasadas (para permitir recuperar datos antiguos)
            return createAppointmentFromPersistence(id, patientId, doctorId, dateTime, durationSlots, status, notes);
        } catch (Exception e) {
            System.err.println("Error al mapear documento a Appointment: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Crea una cita desde datos de persistencia sin validar fechas pasadas
     * Este método es usado para recuperar datos existentes, incluidos los antiguos que pueden tener fechas pasadas
     */
    private Appointment createAppointmentFromPersistence(String id, String patientId, String doctorId, 
                                                       LocalDateTime dateTime, int durationSlots, 
                                                       AppointmentStatus status, String notes) {
        // Crear instancia usando el constructor vacío
        Appointment appointment = new Appointment();
        
        // Asignar valores directamente sin pasar por las validaciones del constructor
        appointment.setId(id);
        appointment.setPatientIdDirect(patientId);
        appointment.setDoctorIdDirect(doctorId);
        appointment.setDateTimeDirect(dateTime);
        appointment.setDurationSlotsDirect(durationSlots);
        appointment.setStatus(status != null ? status : AppointmentStatus.PENDIENTE);
        appointment.setNotes(notes);
        
        return appointment;
    }
    
    private Object mapToFirestore(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        
        // Crear un mapa con los datos de la cita
        Map<String, Object> data = new HashMap<>();
        data.put("patientId", appointment.getPatientId());
        data.put("doctorId", appointment.getDoctorId());
        data.put("dateTime", appointment.getDateTime().toString());
        data.put("durationSlots", appointment.getDurationSlots());
        data.put("status", appointment.getStatus().toString());
        
        // Añadir notas solo si existen
        if (appointment.getNotes() != null && !appointment.getNotes().trim().isEmpty()) {
            data.put("notes", appointment.getNotes());
        }
        
        return data;
    }
} 