package odoonto.domain.repository;

import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.entities.Tooth;
import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.OdontogramId;
import odoonto.domain.model.valueobjects.PatientId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del repositorio para gestionar Odontogramas.
 * Nota: Los odontogramas se identifican por el ID del paciente al que pertenecen.
 */
public interface OdontogramRepository {
    
    /**
     * Busca todos los odontogramas
     * @return Flux con todos los odontogramas
     */
    Flux<Odontogram> findAll();
    
    /**
     * Busca un odontograma por su identificador
     * @param id Identificador único del odontograma
     * @return Mono con el odontograma encontrado o empty si no existe
     */
    Mono<Odontogram> findById(OdontogramId id);
    
    /**
     * Busca un odontograma por el identificador del paciente
     * @param patientId Identificador único del paciente
     * @return Mono con el odontograma encontrado o empty si no existe
     */
    Mono<Odontogram> findByPatientId(PatientId patientId);
    
    /**
     * Guarda un odontograma en el repositorio
     * @param odontogram Odontograma a guardar
     * @return Mono con el odontograma guardado
     */
    Mono<Odontogram> save(Odontogram odontogram);
    
    /**
     * Elimina un odontograma por su identificador
     * @param id Identificador único del odontograma
     * @return Mono vacío que completa cuando la operación termina
     */
    Mono<Void> deleteById(OdontogramId id);
    
    /**
     * Busca odontogramas que contienen un tipo específico de lesión
     * @param lesionType Tipo de lesión a buscar
     * @return Flux con los odontogramas que contienen el tipo de lesión especificado
     */
    Flux<Odontogram> findByLesionType(LesionType lesionType);
    
    /**
     * Verifica si existe un odontograma para el paciente especificado
     * @param patientId Identificador único del paciente
     * @return Mono con true si existe o false si no existe
     */
    Mono<Boolean> existsByPatientId(PatientId patientId);
    
    /**
     * Elimina un odontograma por el identificador del paciente
     * @param patientId Identificador único del paciente
     * @return Mono vacío que completa cuando la operación termina
     */
    Mono<Void> deleteByPatientId(PatientId patientId);
    
    /**
     * Crea una copia histórica de un odontograma
     * @param odontogramId Identificador único del odontograma a copiar
     * @return Mono con el ID de la versión histórica creada
     */
    Mono<String> createHistoricalCopy(OdontogramId odontogramId);
    
    /**
     * Busca todas las versiones históricas de un odontograma por el identificador del paciente
     * @param patientId Identificador único del paciente
     * @return Flux con las versiones históricas del odontograma
     */
    Flux<Odontogram> findHistoryByPatientId(PatientId patientId);
    
    /**
     * Busca una versión histórica específica de un odontograma
     * @param patientId Identificador único del paciente
     * @param version Identificador de versión
     * @return Mono con la versión histórica del odontograma o empty si no existe
     */
    Mono<Odontogram> findHistoricalByPatientIdAndVersion(PatientId patientId, String version);
    
    /**
     * Actualiza un diente específico en el odontograma de un paciente
     * @param patientId ID del paciente
     * @param toothNumber Número del diente
     * @param tooth Diente actualizado
     * @return true si se actualizó correctamente
     */
    boolean updateTooth(String patientId, String toothNumber, Tooth tooth);
    
    /* 
     * Métodos de compatibilidad para mantener retrocompatibilidad
     */
     
    /**
     * Método de compatibilidad para buscar por id usando String
     * @param id Identificador como String
     * @return Mono con el odontograma encontrado o empty si no existe
     */
    default Mono<Odontogram> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Mono.empty();
        }
        return findById(OdontogramId.of(id));
    }
    
    /**
     * Método de compatibilidad para buscar por patientId usando String
     * @param patientId Identificador del paciente como String
     * @return Mono con el odontograma encontrado o empty si no existe
     */
    default Mono<Odontogram> findByPatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return Mono.empty();
        }
        return findByPatientId(PatientId.of(patientId));
    }
    
    /**
     * Método de compatibilidad para eliminar por id usando String
     * @param id Identificador como String
     * @return Mono vacío que completa cuando la operación termina
     */
    default Mono<Void> deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Mono.empty();
        }
        return deleteById(OdontogramId.of(id));
    }
    
    /**
     * Método de compatibilidad para verificar existencia por patientId usando String
     * @param patientId Identificador del paciente como String
     * @return Mono con true si existe o false si no existe
     */
    default Mono<Boolean> existsByPatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return Mono.just(false);
        }
        return existsByPatientId(PatientId.of(patientId));
    }
    
    /**
     * Método de compatibilidad para eliminar por patientId usando String
     * @param patientId Identificador del paciente como String
     * @return Mono vacío que completa cuando la operación termina
     */
    default Mono<Void> deleteByPatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return Mono.empty();
        }
        return deleteByPatientId(PatientId.of(patientId));
    }
    
    /**
     * Método de compatibilidad para crear copia histórica usando String
     * @param odontogramId Identificador del odontograma como String
     * @return Mono con el ID de la versión histórica creada
     */
    default Mono<String> createHistoricalCopy(String odontogramId) {
        if (odontogramId == null || odontogramId.trim().isEmpty()) {
            return Mono.empty();
        }
        return createHistoricalCopy(OdontogramId.of(odontogramId));
    }
    
    /**
     * Método de compatibilidad para buscar historia por patientId usando String
     * @param patientId Identificador del paciente como String
     * @return Flux con las versiones históricas del odontograma
     */
    default Flux<Odontogram> findHistoryByPatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return Flux.empty();
        }
        return findHistoryByPatientId(PatientId.of(patientId));
    }
    
    /**
     * Método de compatibilidad para buscar versión histórica usando String
     * @param patientId Identificador del paciente como String
     * @param version Identificador de versión
     * @return Mono con la versión histórica del odontograma o empty si no existe
     */
    default Mono<Odontogram> findHistoricalByPatientIdAndVersion(String patientId, String version) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return Mono.empty();
        }
        return findHistoricalByPatientIdAndVersion(PatientId.of(patientId), version);
    }
} 