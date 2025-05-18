package odoonto.infrastructure.persistence.reactive;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.api.core.ApiFuture;

import odoonto.application.port.out.ReactivePatientRepository;
import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.model.valueobjects.PatientId;
import odoonto.infrastructure.persistence.entity.FirestorePatientEntity;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CompletableFuture;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación reactiva del repositorio de pacientes usando Firestore
 */
@Component
public class ReactivePatientRepositoryAdapter implements ReactivePatientRepository {

    private static final String COLLECTION_NAME = "patients";
    
    private final CollectionReference patientsCollection;

    /**
     * Constructor
     * @param firestore Cliente Firestore
     */
    public ReactivePatientRepositoryAdapter(Firestore firestore) {
        this.patientsCollection = firestore.collection(COLLECTION_NAME);
    }

    @Override
    public Mono<Patient> findById(String id) {
        if (id == null) {
            return Mono.empty();
        }
        
        return Mono.fromCallable(() -> {
            ApiFuture<DocumentSnapshot> future = patientsCollection.document(id).get();
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
        .filter(DocumentSnapshot::exists)
        .map(this::mapToPatient)
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Mono<Patient> findById(PatientId patientId) {
        if (patientId == null) {
            return Mono.empty();
        }
        return findById(patientId.getValue());
    }

    @Override
    public Mono<Patient> save(Patient patient) {
        if (patient == null) {
            return Mono.error(new IllegalArgumentException("El paciente no puede ser nulo"));
        }
        
        // Asegurar que el paciente tenga un ID
        if (patient.getId() == null) {
            patient.setId(PatientId.generate());
        }
        
        FirestorePatientEntity entity = mapToEntity(patient);
        
        return Mono.fromCallable(() -> {
            ApiFuture<?> future = patientsCollection.document(entity.getId()).set(entity);
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            
            future.addListener(() -> {
                try {
                    completableFuture.complete(future.get());
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
            }, Runnable::run);
            
            return completableFuture.thenApply(result -> patient);
        })
        .flatMap(future -> Mono.fromFuture(future))
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteById(String id) {
        if (id == null) {
            return Mono.empty();
        }
        
        return Mono.fromCallable(() -> {
            ApiFuture<?> future = patientsCollection.document(id).delete();
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
    public Flux<Patient> findAll() {
        return Mono.fromCallable(() -> {
            ApiFuture<QuerySnapshot> future = patientsCollection.get();
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
            return Flux.fromIterable(querySnapshot.getDocuments())
                .map(this::mapToPatient)
                .filter(patient -> patient != null);
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Patient> findByNameContaining(String name) {
        // Firestore no soporta búsquedas parciales nativas, 
        // esta es una implementación simple que podría no ser eficiente.
        return findAll()
                .filter(patient -> 
                    (patient.getNombre() != null && 
                     patient.getNombre().toLowerCase().contains(name.toLowerCase())) ||
                    (patient.getApellido() != null && 
                     patient.getApellido().toLowerCase().contains(name.toLowerCase()))
                );
    }

    @Override
    public Flux<Patient> findByPhoneContaining(String phone) {
        // Firestore no soporta búsquedas parciales nativas,
        // esta es una implementación simple que podría no ser eficiente.
        return findAll()
                .filter(patient -> 
                    patient.getTelefono() != null && 
                    patient.getTelefono().getValue().contains(phone)
                );
    }
    
    @Override
    public Flux<Patient> findByAddressContaining(String address) {
        // Implementación simplificada - asumimos que no hay campo de dirección
        // o que se buscaría en otros campos
        return Flux.empty();
    }
    
    @Override
    public Mono<Boolean> existsById(String id) {
        if (id == null) {
            return Mono.just(false);
        }
        
        return Mono.fromCallable(() -> {
            ApiFuture<DocumentSnapshot> future = patientsCollection.document(id).get();
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
        .map(DocumentSnapshot::exists)
        .subscribeOn(Schedulers.boundedElastic());
    }
    
    /**
     * Mapea un documento de Firestore a una entidad de dominio Patient
     */
    private Patient mapToPatient(DocumentSnapshot document) {
        if (!document.exists()) {
            return null;
        }
        
        try {
            String id = document.getId();
            
            // Crear un paciente vacío 
            Patient patient = new Patient();
            
            // Asignar ID
            patient.setId(PatientId.of(id));
            
            // Obtener y asignar propiedades de forma segura
            // Verificar el tipo de cada campo antes de intentar obtenerlo
            try {
                // Propiedades básicas
                if (document.contains("nombre") && document.get("nombre") instanceof String) {
                    patient.setNombre(document.getString("nombre"));
                }
                
                if (document.contains("apellido") && document.get("apellido") instanceof String) {
                    patient.setApellido(document.getString("apellido"));
                }
                
                // Fecha de nacimiento
                if (document.contains("fechaNacimiento") && document.get("fechaNacimiento") instanceof String) {
                    String fechaNacimientoStr = document.getString("fechaNacimiento");
                    if (fechaNacimientoStr != null) {
                        try {
                            // Intentar primero el formato simple yyyy-MM-dd
                            patient.setFechaNacimiento(java.time.LocalDate.parse(fechaNacimientoStr));
                        } catch (java.time.format.DateTimeParseException e) {
                            try {
                                // Si falla, intentar parsear formatos con tiempo (yyyy-MM-ddTHH:mm:ssZ)
                                // Extraer solo la parte de la fecha (los primeros 10 caracteres)
                                if (fechaNacimientoStr.length() >= 10) {
                                    String soloFecha = fechaNacimientoStr.substring(0, 10);
                                    patient.setFechaNacimiento(java.time.LocalDate.parse(soloFecha));
                                }
                            } catch (Exception ex) {
                                System.err.println("No se pudo parsear la fecha: " + fechaNacimientoStr);
                            }
                        }
                    }
                }
                
                // Sexo
                if (document.contains("sexo") && document.get("sexo") instanceof String) {
                    String sexoStr = document.getString("sexo");
                    patient.setSexo(odoonto.domain.model.valueobjects.Sexo.valueOf(sexoStr));
                }
                
                // Teléfono - puede ser un String o un Map
                if (document.contains("telefono")) {
                    Object telefono = document.get("telefono");
                    if (telefono != null) {
                        if (telefono instanceof String) {
                            // Es un string directo
                            String telefonoStr = (String)telefono;
                            if (telefonoStr != null && !telefonoStr.isEmpty()) {
                                patient.setTelefono(new odoonto.domain.model.valueobjects.PhoneNumber(telefonoStr));
                            }
                        } else if (telefono instanceof Map) {
                            // Es un mapa, intentar obtener el valor como una cadena
                            @SuppressWarnings("unchecked")
                            Map<String, Object> telefonoMap = (Map<String, Object>) telefono;
                            if (telefonoMap.containsKey("value")) {
                                Object valueObj = telefonoMap.get("value");
                                if (valueObj != null) {
                                    String telefonoStr = valueObj.toString();
                                    if (telefonoStr != null) {
                                        patient.setTelefono(new odoonto.domain.model.valueobjects.PhoneNumber(telefonoStr));
                                    }
                                }
                            } else {
                                System.err.println("Mapa de teléfono no tiene campo 'value' para paciente " + id);
                            }
                        } else {
                            System.err.println("Campo teléfono tiene tipo inesperado para paciente " + id + ": " + telefono.getClass().getName());
                        }
                    }
                }
                
                // Email - puede ser un String o un Map
                if (document.contains("email")) {
                    Object email = document.get("email");
                    if (email != null) {
                        if (email instanceof String) {
                            // Es un string directo
                            String emailStr = (String)email;
                            if (emailStr != null && !emailStr.isEmpty()) {
                                patient.setEmail(new odoonto.domain.model.valueobjects.EmailAddress(emailStr));
                            }
                        } else if (email instanceof Map) {
                            // Es un mapa, intentar obtener el valor como una cadena
                            @SuppressWarnings("unchecked")
                            Map<String, Object> emailMap = (Map<String, Object>) email;
                            if (emailMap.containsKey("value")) {
                                Object valueObj = emailMap.get("value");
                                if (valueObj != null) {
                                    String emailStr = valueObj.toString();
                                    if (emailStr != null) {
                                        patient.setEmail(new odoonto.domain.model.valueobjects.EmailAddress(emailStr));
                                    }
                                }
                            } else {
                                System.err.println("Mapa de email no tiene campo 'value' para paciente " + id);
                            }
                        } else {
                            System.err.println("Campo email tiene tipo inesperado para paciente " + id + ": " + email.getClass().getName());
                        }
                    }
                }
                
            } catch (Exception e) {
                System.err.println("Error al convertir propiedades del paciente " + id + ": " + e.getMessage());
                e.printStackTrace();
                // Si hay error en convertir propiedades, aún devolvemos el paciente con lo que pudimos asignar
            }
            
            return patient;
        } catch (Exception e) {
            System.err.println("Error al mapear documento a Patient: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Mapea una entidad de dominio Patient a una entidad de persistencia FirestorePatientEntity
     */
    private FirestorePatientEntity mapToEntity(Patient patient) {
        FirestorePatientEntity entity = new FirestorePatientEntity();
        entity.setId(patient.getIdValue());
        entity.setNombre(patient.getNombre());
        entity.setApellido(patient.getApellido());
        
        // Mapear fechaNacimiento a formato ISO
        if (patient.getFechaNacimiento() != null) {
            entity.setFechaNacimiento(patient.getFechaNacimiento().toString());
        }
        
        // Mapear sexo a string
        if (patient.getSexo() != null) {
            entity.setSexo(patient.getSexo().toString());
        }
        
        // Mapear teléfono y email a string
        if (patient.getTelefono() != null) {
            entity.setTelefono(patient.getTelefono().getValue());
        }
        
        if (patient.getEmail() != null) {
            entity.setEmail(patient.getEmail().getValue());
        }
        
        // Crear referencias al odontograma y al historial médico
        Map<String, Object> odontogramaRef = new HashMap<>();
        odontogramaRef.put("id", "odontogram_" + patient.getIdValue());
        entity.setOdontogramaRef(odontogramaRef);
        
        Map<String, Object> historialRef = new HashMap<>();
        historialRef.put("id", "medical_record_" + patient.getIdValue());
        entity.setHistorialMedicoRef(historialRef);
        
        return entity;
    }
} 