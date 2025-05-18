package odoonto.infrastructure.persistence.reactive;

import odoonto.application.port.out.ReactiveDoctorRepository;
import odoonto.domain.model.aggregates.Doctor;
import odoonto.domain.model.valueobjects.Specialty;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.DocumentReference;
import com.google.api.core.ApiFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Adaptador que implementa la interfaz reactiva para el repositorio de doctores.
 * Implementa directamente las operaciones reactivas con Firestore.
 */
@Component
public class ReactiveDoctorRepositoryAdapter implements ReactiveDoctorRepository {

    private final CollectionReference doctorsCollection;

    /**
     * Constructor que recibe la instancia de Firestore
     * @param firestore Instancia de Firestore para acceder a la base de datos
     */
    public ReactiveDoctorRepositoryAdapter(Firestore firestore) {
        this.doctorsCollection = firestore.collection("doctors");
    }
    
    @Override
    public Mono<Doctor> findById(String id) {
        return Mono.fromCallable(() -> {
            DocumentReference docRef = doctorsCollection.document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
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
        .map(this::mapToDoctor)
        .filter(doctor -> doctor != null)
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Mono<Doctor> save(Doctor doctor) {
        return Mono.fromCallable(() -> {
            Map<String, Object> docData = mapToFirestore(doctor);
            String docId = doctor.getId().toString();
            
            ApiFuture<?> future = doctorsCollection.document(docId).set(docData);
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            
            future.addListener(() -> {
                try {
                    completableFuture.complete(future.get());
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
            }, Runnable::run);
            
            return completableFuture.thenApply(result -> doctor);
        })
        .flatMap(future -> Mono.fromFuture(future))
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        // Validar que el ID no sea nulo o vacío
        if (id == null || id.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID del doctor no puede ser nulo o vacío"));
        }
        
        return Mono.fromCallable(() -> {
            ApiFuture<?> future = doctorsCollection.document(id).delete();
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
    public Flux<Doctor> findAll() {
        return Mono.fromCallable(() -> {
            ApiFuture<QuerySnapshot> future = doctorsCollection.get();
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
            List<Doctor> doctors = new ArrayList<>();
            querySnapshot.getDocuments().forEach(doc -> {
                Doctor doctor = mapToDoctor(doc);
                if (doctor != null) {
                    doctors.add(doctor);
                }
            });
            return Flux.fromIterable(doctors);
        })
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Flux<Doctor> findByEspecialidad(Specialty especialidad) {
        return Mono.fromCallable(() -> {
            ApiFuture<QuerySnapshot> future = doctorsCollection
                .whereEqualTo("especialidad", especialidad.toString())
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
        .flatMapMany(this::mapQuerySnapshotToFlux)
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Flux<Doctor> findByNombreCompletoContaining(String nombre) {
        // Firestore no admite búsquedas LIKE directamente, así que implementamos una solución alternativa
        // Recuperamos todos los doctores y filtramos en memoria aquellos cuyos nombres contienen el texto de búsqueda
        return findAll()
            .filter(doctor -> doctor.getNombreCompleto() != null && 
                doctor.getNombreCompleto().toLowerCase().contains(nombre.toLowerCase()))
            .subscribeOn(Schedulers.boundedElastic());
    }
    
    // Métodos auxiliares para mapeo
    
    private Flux<Doctor> mapQuerySnapshotToFlux(QuerySnapshot querySnapshot) {
        List<Doctor> doctors = new ArrayList<>();
        querySnapshot.getDocuments().forEach(doc -> {
            Doctor doctor = mapToDoctor(doc);
            if (doctor != null) {
                doctors.add(doctor);
            }
        });
        return Flux.fromIterable(doctors);
    }
    
    private Doctor mapToDoctor(DocumentSnapshot document) {
        if (!document.exists()) {
            return null;
        }
        
        try {
            // Obtener datos del documento
            String id = document.getId();
            String nombreCompleto = document.getString("nombreCompleto");
            String especialidadStr = document.getString("especialidad");
            
            // Usar el método fromNombre para convertir a Specialty de manera más robusta
            Specialty especialidad = Specialty.fromNombre(especialidadStr);
            
            // Crear y devolver un nuevo Doctor con los datos obtenidos
            return new Doctor(id, nombreCompleto, especialidad);
        } catch (Exception e) {
            // Registrar el error y devolver null
            System.err.println("Error al mapear documento a Doctor: " + e.getMessage());
            e.printStackTrace(); // Agregar stack trace para más detalle
            return null;
        }
    }
    
    private Map<String, Object> mapToFirestore(Doctor doctor) {
        Map<String, Object> docData = new HashMap<>();
        
        // Guardar los datos del doctor
        docData.put("nombreCompleto", doctor.getNombreCompleto());
        if (doctor.getEspecialidad() != null) {
            docData.put("especialidad", doctor.getEspecialidad().toString());
        }
        
        // Podríamos añadir más campos si es necesario
        
        return docData;
    }
} 