package odoonto.infrastructure.persistence.firestore;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.HashMap;
import java.time.Instant;
import java.util.UUID;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.entities.Tooth;
import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.OdontogramId;
import odoonto.domain.model.valueobjects.PatientId;
import odoonto.domain.repository.OdontogramRepository;
import odoonto.infrastructure.persistence.entity.FirestoreOdontogramEntity;

/**
 * Implementación de OdontogramRepository para Firestore que aprovecha
 * el patrón de identidad compartida entre Patient y Odontogram.
 */
@Repository
public class FirestoreOdontogramRepository implements OdontogramRepository {

    private final Firestore firestore;
    private final CollectionReference odontogramsCollection;
    private final CollectionReference historicalOdontogramsCollection;
    
    @Autowired
    public FirestoreOdontogramRepository(Firestore firestore) {
        this.firestore = firestore;
        this.odontogramsCollection = firestore.collection("odontograms");
        this.historicalOdontogramsCollection = firestore.collection("historical_odontograms");
    }
    
    @Override
    public Flux<Odontogram> findAll() {
        try {
            List<QueryDocumentSnapshot> documents = odontogramsCollection.get().get().getDocuments();
            return Flux.fromIterable(documents)
                    .map(this::mapToOdontogram);
        } catch (Exception e) {
            return Flux.empty();
        }
    }
    
    @Override
    public Mono<Odontogram> findById(OdontogramId id) {
        if (id == null) {
            return Mono.empty();
        }
        
        try {
            ApiFuture<DocumentSnapshot> future = odontogramsCollection.document(id.getValue()).get();
            CompletableFuture<DocumentSnapshot> completableFuture = toCompletableFuture(future);
            return Mono.fromFuture(completableFuture)
                    .map(this::mapToOdontogram)
                    .filter(odontogram -> odontogram != null);
        } catch (Exception e) {
            return Mono.empty();
        }
    }
    
    @Override
    public Mono<Odontogram> findByPatientId(PatientId patientId) {
        if (patientId == null) {
            return Mono.empty();
        }
        
        // Crear el ID del odontograma a partir del ID del paciente
        OdontogramId odontogramId = OdontogramId.fromPatientId(patientId);
        
        // Buscar directamente por ID derivado, evitando una consulta por campo
        return findById(odontogramId);
    }
    
    @Override
    public Mono<Odontogram> save(Odontogram odontogram) {
        if (odontogram == null) {
            return Mono.empty();
        }
        
        // Asegurar que el odontograma tenga un ID
        if (odontogram.getId() == null) {
            throw new DomainException("El odontograma debe tener un ID antes de ser guardado");
        }
        
        FirestoreOdontogramEntity entity = mapToEntity(odontogram);
        
        // Fecha de actualización
        entity.setFechaActualizacion(Instant.now().toString());
        
        // Si es un nuevo documento, establecer la fecha de creación
        if (entity.getFechaCreacion() == null) {
            entity.setFechaCreacion(entity.getFechaActualizacion());
        }
        
        try {
            ApiFuture<WriteResult> future = odontogramsCollection.document(entity.getId()).set(entity);
            CompletableFuture<WriteResult> completableFuture = toCompletableFuture(future);
            return Mono.fromFuture(completableFuture)
                    .thenReturn(odontogram);
        } catch (Exception e) {
            return Mono.error(new DomainException("Error al guardar odontograma: " + e.getMessage(), e));
        }
    }
    
    @Override
    public Mono<Void> deleteById(OdontogramId id) {
        if (id == null) {
            return Mono.empty();
        }
        
        try {
            ApiFuture<WriteResult> future = odontogramsCollection.document(id.getValue()).delete();
            CompletableFuture<WriteResult> completableFuture = toCompletableFuture(future);
            return Mono.fromFuture(completableFuture)
                    .then();
        } catch (Exception e) {
            return Mono.error(new DomainException("Error al eliminar odontograma: " + e.getMessage(), e));
        }
    }
    
    @Override
    public Flux<Odontogram> findByLesionType(LesionType lesionType) {
        if (lesionType == null) {
            return Flux.empty();
        }

        // Esta es una consulta compleja en Firestore
        // ya que requiere buscar en una estructura anidada (dientes -> caras -> lesiones)
        // Implementación simplificada: recuperamos todos y filtramos en memoria
        return findAll()
                .filter(odontogram -> odontogram.getTeeth().values().stream()
                        .anyMatch(tooth -> tooth.getFaces().values().stream()
                                .anyMatch(lesion -> lesion != null && lesion.toString().equals(lesionType.toString()))));
    }

    @Override
    public Mono<Boolean> existsByPatientId(PatientId patientId) {
        if (patientId == null) {
            return Mono.just(false);
        }
        
        OdontogramId odontogramId = OdontogramId.fromPatientId(patientId);
        try {
            ApiFuture<DocumentSnapshot> future = odontogramsCollection.document(odontogramId.getValue()).get();
            CompletableFuture<DocumentSnapshot> completableFuture = toCompletableFuture(future);
            return Mono.fromFuture(completableFuture)
                    .map(DocumentSnapshot::exists);
        } catch (Exception e) {
            return Mono.just(false);
        }
    }

    @Override
    public Mono<Void> deleteByPatientId(PatientId patientId) {
        if (patientId == null) {
            return Mono.empty();
        }
        
        OdontogramId odontogramId = OdontogramId.fromPatientId(patientId);
        return deleteById(odontogramId);
    }

    @Override
    public Mono<String> createHistoricalCopy(OdontogramId odontogramId) {
        if (odontogramId == null) {
            return Mono.empty();
        }
        
        return findById(odontogramId)
                .flatMap(odontogram -> {
                    // Crear una copia con los mismos datos pero diferente ID
                    FirestoreOdontogramEntity entity = mapToEntity(odontogram);
                    String historicalId = odontogramId.getValue() + "_" + UUID.randomUUID().toString();
                    entity.setId(historicalId);
                    entity.setFechaCreacion(Instant.now().toString());
                    entity.setFechaActualizacion(entity.getFechaCreacion());
                    
                    // Añadir metadatos históricos como campos adicionales
                    Map<String, Object> dientesMap = entity.getDientes();
                    if (dientesMap == null) {
                        dientesMap = new HashMap<>();
                    }
                    
                    // Añadir información histórica al mapa de dientes
                    dientesMap.put("_metadata", Map.of(
                        "esHistorico", true,
                        "odontogramaOriginalId", odontogramId.getValue()
                    ));
                    entity.setDientes(dientesMap);
                    
                    try {
                        ApiFuture<WriteResult> future = historicalOdontogramsCollection.document(historicalId).set(entity);
                        CompletableFuture<WriteResult> completableFuture = toCompletableFuture(future);
                        return Mono.fromFuture(completableFuture)
                                .thenReturn(historicalId);
                    } catch (Exception e) {
                        return Mono.error(new DomainException("Error al crear copia histórica: " + e.getMessage(), e));
                    }
                });
    }

    @Override
    public Flux<Odontogram> findHistoryByPatientId(PatientId patientId) {
        if (patientId == null) {
            return Flux.empty();
        }
        
        OdontogramId odontogramId = OdontogramId.fromPatientId(patientId);
        
        try {
            List<QueryDocumentSnapshot> documents = historicalOdontogramsCollection
                    .whereEqualTo("dientes._metadata.odontogramaOriginalId", odontogramId.getValue())
                    .orderBy("fecha_creacion", Query.Direction.DESCENDING)
                    .get()
                    .get()
                    .getDocuments();
            
            return Flux.fromIterable(documents)
                    .map(this::mapToOdontogram);
        } catch (Exception e) {
            return Flux.empty();
        }
    }

    @Override
    public Mono<Odontogram> findHistoricalByPatientIdAndVersion(PatientId patientId, String version) {
        if (patientId == null || version == null || version.trim().isEmpty()) {
            return Mono.empty();
        }
        
        OdontogramId odontogramId = OdontogramId.fromPatientId(patientId);
        String historicalId = odontogramId.getValue() + "_" + version;
        
        try {
            ApiFuture<DocumentSnapshot> future = historicalOdontogramsCollection.document(historicalId).get();
            CompletableFuture<DocumentSnapshot> completableFuture = toCompletableFuture(future);
            return Mono.fromFuture(completableFuture)
                    .map(this::mapToOdontogram)
                    .filter(odontogram -> odontogram != null);
        } catch (Exception e) {
            return Mono.empty();
        }
    }

    @Override
    public boolean updateTooth(String patientId, String toothNumber, Tooth tooth) {
        if (patientId == null || toothNumber == null || tooth == null) {
            return false;
        }
        
        try {
            // Obtener el odontograma
            DocumentSnapshot document = odontogramsCollection
                    .document("odontogram_" + patientId)
                    .get()
                    .get();
            
            if (!document.exists()) {
                return false;
            }
            
            // Actualizar solo el diente específico, sin cargar todo el objeto
            Map<String, Object> toothData = new HashMap<>();
            // Mapear las lesiones a un formato adecuado para Firestore
            Map<String, Object> facesMap = new HashMap<>();
            tooth.getLesions().forEach((face, lesion) -> 
                facesMap.put(face, lesion.getType().toString()));
            
            toothData.put("faces", facesMap);
            
            // También guardar información de tratamientos si es necesario
            
            // Actualizar en Firestore
            Map<String, Object> update = new HashMap<>();
            update.put("dientes." + toothNumber, toothData);
            update.put("fechaActualizacion", Instant.now().toString());
            
            odontogramsCollection.document("odontogram_" + patientId)
                    .update(update)
                    .get();
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Método de conveniencia para crear un nuevo odontograma para un paciente
     * @param patientId ID del paciente
     * @return Nuevo odontograma
     */
    public Mono<Odontogram> createForPatient(PatientId patientId) {
        if (patientId == null) {
            return Mono.empty();
        }
        
        // Crear un nuevo odontograma con ID derivado del paciente
        Odontogram odontogram = new Odontogram(patientId);
        
        // Guardar y retornar
        return save(odontogram);
    }
    
    /**
     * Convierte ApiFuture a CompletableFuture
     */
    private <T> CompletableFuture<T> toCompletableFuture(ApiFuture<T> apiFuture) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        apiFuture.addListener(() -> {
            try {
                completableFuture.complete(apiFuture.get());
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        }, Runnable::run);
        return completableFuture;
    }
    
    /**
     * Mapea un documento de Firestore a una entidad de dominio Odontogram
     */
    private Odontogram mapToOdontogram(DocumentSnapshot document) {
        FirestoreOdontogramEntity entity = document.toObject(FirestoreOdontogramEntity.class);
        if (entity == null) {
            return null;
        }
        
        OdontogramId id = OdontogramId.of(entity.getId());
        
        // Extraer el ID del paciente del ID del odontograma si es posible
        PatientId patientId = id.extractPatientId();
        
        // Convertir el mapa de dientes a objetos de dominio
        Map<String, Odontogram.ToothRecord> teethMap = new HashMap<>();
        Map<String, Object> dientesMap = entity.getDientes();
        
        if (dientesMap != null) {
            for (Map.Entry<String, Object> entry : dientesMap.entrySet()) {
                String toothId = entry.getKey();
                
                // Ignorar entrada de metadatos
                if (toothId.equals("_metadata")) {
                    continue;
                }
                
                @SuppressWarnings("unchecked")
                Map<String, Object> toothData = (Map<String, Object>) entry.getValue();
                
                if (toothData == null) {
                    continue;
                }
                
                // Mapear las caras del diente a tipos de lesión
                @SuppressWarnings("unchecked")
                Map<String, String> facesMap = (Map<String, String>) toothData.get("faces");
                
                // Crear y agregar el registro de diente con un nuevo mapa vacío
                Odontogram.ToothRecord toothRecord = new Odontogram.ToothRecord();
                
                // Si hay datos de caras, añadirlos al registro
                if (facesMap != null) {
                    for (Map.Entry<String, String> faceEntry : facesMap.entrySet()) {
                        toothRecord.getFaces().put(faceEntry.getKey(), LesionType.valueOf(faceEntry.getValue()));
                    }
                }
                
                teethMap.put(toothId, toothRecord);
            }
        }
        
        // Crear y retornar el odontograma
        return new Odontogram(id, teethMap);
    }
    
    /**
     * Mapea una entidad de dominio Odontogram a una entidad de persistencia FirestoreOdontogramEntity
     */
    private FirestoreOdontogramEntity mapToEntity(Odontogram odontogram) {
        FirestoreOdontogramEntity entity = new FirestoreOdontogramEntity();
        entity.setId(odontogram.getIdValue());
        
        // Mapear dientes a un mapa
        Map<String, Object> dientesMap = new HashMap<>();
        Map<String, Odontogram.ToothRecord> teeth = odontogram.getTeeth();
        
        for (Map.Entry<String, Odontogram.ToothRecord> entry : teeth.entrySet()) {
            String toothId = entry.getKey();
            Odontogram.ToothRecord toothRecord = entry.getValue();
            
            Map<String, Object> toothData = new HashMap<>();
            
            // Convertir map de lesiones de LesionType a String
            Map<String, String> facesAsString = new HashMap<>();
            for (Map.Entry<String, LesionType> faceEntry : toothRecord.getFaces().entrySet()) {
                facesAsString.put(faceEntry.getKey(), faceEntry.getValue().toString());
            }
            
            toothData.put("faces", facesAsString);
            
            // Agregar el registro de diente al mapa
            dientesMap.put(toothId, toothData);
        }
        
        entity.setDientes(dientesMap);
        
        return entity;
    }
} 