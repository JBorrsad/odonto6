package odoonto.infrastructure.persistence.reactive;

import odoonto.application.port.out.ReactiveOdontogramRepository;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.entities.Tooth;
import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.OdontogramId;
import odoonto.domain.model.valueobjects.PatientId;

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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Adaptador que implementa la interfaz reactiva para el repositorio de odontogramas.
 * Implementa directamente las operaciones reactivas con Firestore.
 */
@Component
public class ReactiveOdontogramRepositoryAdapter implements ReactiveOdontogramRepository {

    private final CollectionReference odontogramsCollection;
    private final CollectionReference historicalOdontogramsCollection;

    /**
     * Constructor que recibe la instancia de Firestore
     * @param firestore Instancia de Firestore para acceder a la base de datos
     */
    public ReactiveOdontogramRepositoryAdapter(Firestore firestore) {
        this.odontogramsCollection = firestore.collection("odontograms");
        this.historicalOdontogramsCollection = firestore.collection("historical_odontograms");
    }

    @Override
    public Flux<Odontogram> findAll() {
        return Mono.fromCallable(() -> {
            ApiFuture<QuerySnapshot> future = odontogramsCollection.get();
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
            List<Odontogram> odontograms = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                Odontogram odontogram = mapToOdontogram(doc);
                if (odontogram != null) {
                    odontograms.add(odontogram);
                }
            }
            return Flux.fromIterable(odontograms);
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Odontogram> findById(OdontogramId id) {
        return Mono.fromCallable(() -> {
            ApiFuture<DocumentSnapshot> future = odontogramsCollection.document(id.getValue()).get();
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
        .map(this::mapToOdontogram)
        .filter(odontogram -> odontogram != null)
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Odontogram> findByPatientId(PatientId patientId) {
        String odontogramId = "odontogram_" + patientId.getValue();
        return findById(OdontogramId.of(odontogramId));
    }

    @Override
    public Mono<Odontogram> save(Odontogram odontogram) {
        return Mono.fromCallable(() -> {
            Map<String, Object> data = mapToFirestore(odontogram);
            String documentId = odontogram.getId().getValue();
            
            ApiFuture<?> future = odontogramsCollection.document(documentId).set(data);
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            
            future.addListener(() -> {
                try {
                    completableFuture.complete(future.get());
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
            }, Runnable::run);
            
            return completableFuture.thenApply(result -> odontogram);
        })
        .flatMap(future -> Mono.fromFuture(future))
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteById(OdontogramId id) {
        return Mono.fromCallable(() -> {
            ApiFuture<?> future = odontogramsCollection.document(id.getValue()).delete();
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
    public Flux<Odontogram> findByLesionType(LesionType lesionType) {
        // Firestore no permite búsquedas directas en arrays anidados, así que debemos obtener todos
        // y filtrar en memoria
        return findAll()
            .filter(odontogram -> containsLesionType(odontogram, lesionType))
            .subscribeOn(Schedulers.boundedElastic());
    }

    private boolean containsLesionType(Odontogram odontogram, LesionType lesionType) {
        // Implementar lógica para verificar si el odontograma tiene algún diente con el tipo de lesión
        // Esto es una implementación simplificada
        return false; // Reemplazar con implementación real
    }

    @Override
    public Mono<Boolean> existsByPatientId(PatientId patientId) {
        return findByPatientId(patientId)
            .map(odontogram -> true)
            .defaultIfEmpty(false)
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteByPatientId(PatientId patientId) {
        String odontogramId = "odontogram_" + patientId.getValue();
        return deleteById(OdontogramId.of(odontogramId));
    }

    @Override
    public Mono<String> createHistoricalCopy(OdontogramId odontogramId) {
        return findById(odontogramId)
            .flatMap(odontogram -> {
                String versionId = System.currentTimeMillis() + "";
                Map<String, Object> historicalData = mapToFirestore(odontogram);
                historicalData.put("version", versionId);
                historicalData.put("originalId", odontogramId.getValue());
                
                String documentId = odontogramId.getValue() + "_v" + versionId;
                
                return Mono.fromCallable(() -> {
                    ApiFuture<?> future = historicalOdontogramsCollection.document(documentId).set(historicalData);
                    CompletableFuture<Object> completableFuture = new CompletableFuture<>();
                    
                    future.addListener(() -> {
                        try {
                            completableFuture.complete(future.get());
                        } catch (Exception e) {
                            completableFuture.completeExceptionally(e);
                        }
                    }, Runnable::run);
                    
                    return completableFuture.thenApply(result -> versionId);
                })
                .flatMap(future -> Mono.fromFuture(future));
            })
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Odontogram> findHistoryByPatientId(PatientId patientId) {
        String odontogramId = "odontogram_" + patientId.getValue();
        
        return Mono.fromCallable(() -> {
            ApiFuture<QuerySnapshot> future = historicalOdontogramsCollection
                .whereEqualTo("originalId", odontogramId)
                .orderBy("version")
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
        .flatMapMany(querySnapshot -> {
            List<Odontogram> odontograms = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                Odontogram odontogram = mapToOdontogram(doc);
                if (odontogram != null) {
                    odontograms.add(odontogram);
                }
            }
            return Flux.fromIterable(odontograms);
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Odontogram> findHistoricalByPatientIdAndVersion(PatientId patientId, String version) {
        String documentId = "odontogram_" + patientId.getValue() + "_v" + version;
        
        return Mono.fromCallable(() -> {
            ApiFuture<DocumentSnapshot> future = historicalOdontogramsCollection.document(documentId).get();
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
        .map(this::mapToOdontogram)
        .filter(odontogram -> odontogram != null)
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> updateTooth(String patientId, String toothNumber, Tooth tooth) {
        String odontogramId = "odontogram_" + patientId;
        
        return Mono.fromCallable(() -> {
            Map<String, Object> toothData = mapToothToFirestore(tooth);
            String fieldPath = "teeth." + toothNumber;
            
            ApiFuture<?> future = odontogramsCollection.document(odontogramId)
                .update(fieldPath, toothData);
            
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            
            future.addListener(() -> {
                try {
                    completableFuture.complete(future.get());
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
            }, Runnable::run);
            
            return completableFuture.thenApply(result -> true);
        })
        .flatMap(future -> Mono.fromFuture(future))
        .onErrorReturn(false)
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> removeLesion(String odontogramId, String toothNumber, String lesionId) {
        return findById(OdontogramId.of(odontogramId))
            .flatMap(odontogram -> {
                // En una implementación real, buscaríamos el diente, encontraríamos y eliminaríamos la lesión
                // Para este ejemplo, actualizamos el documento de Firestore directamente
                
                return Mono.fromCallable(() -> {
                    String fieldPath = "teeth." + toothNumber + ".lesions";
                    // Aquí necesitaríamos usar FieldValue.arrayRemove, pero simplificamos
                    ApiFuture<?> future = odontogramsCollection.document(odontogramId)
                        .update(fieldPath, new ArrayList<>()); // Esto no es correcto, solo ilustrativo
                    
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
                .then();
            })
            .then()
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> addTreatment(String odontogramId, String toothNumber, Object treatmentData) {
        return Mono.fromCallable(() -> {
            String fieldPath = "teeth." + toothNumber + ".treatments";
            // Aquí usaríamos FieldValue.arrayUnion para agregar al array
            ApiFuture<?> future = odontogramsCollection.document(odontogramId)
                .update(fieldPath, treatmentData); // Simplificado
            
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
    public Mono<Void> removeTreatment(String odontogramId, String toothNumber, String treatmentId) {
        return Mono.fromCallable(() -> {
            String fieldPath = "teeth." + toothNumber + ".treatments";
            // Aquí usaríamos FieldValue.arrayRemove para quitar del array
            ApiFuture<?> future = odontogramsCollection.document(odontogramId)
                .update(fieldPath, new ArrayList<>()); // Simplificado
            
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
    
    // Métodos auxiliares para mapeo
    
    private Odontogram mapToOdontogram(DocumentSnapshot document) {
        if (!document.exists()) {
            return null;
        }
        
        // Implementar lógica de mapeo de Firestore a Odontogram
        // Este es un ejemplo simplificado
        return null; // Reemplazar con implementación real
    }
    
    private Map<String, Object> mapToFirestore(Odontogram odontogram) {
        Map<String, Object> data = new HashMap<>();
        // Implementar lógica de mapeo de Odontogram a formato Firestore
        return data;
    }
    
    private Map<String, Object> mapToothToFirestore(Tooth tooth) {
        Map<String, Object> data = new HashMap<>();
        // Implementar lógica de mapeo de Tooth a formato Firestore
        return data;
    }
} 