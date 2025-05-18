package odoonto.application.port.out;

import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.entities.Tooth;
import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.OdontogramId;
import odoonto.domain.model.valueobjects.PatientId;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Puerto de salida reactivo para el repositorio de odontogramas.
 * Esta interfaz adapta el repositorio de dominio a una interfaz reactiva
 * para su uso en la capa de aplicación.
 */
public interface ReactiveOdontogramRepository {
    
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
     * @return Mono que completa cuando la operación termina
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
     * @return Mono que completa cuando la operación termina
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
     * @return Mono con true si se actualizó correctamente
     */
    Mono<Boolean> updateTooth(String patientId, String toothNumber, Tooth tooth);
    
    /**
     * Elimina una lesión específica de un diente en un odontograma por ID
     * @param odontogramId ID del odontograma
     * @param toothNumber Número del diente
     * @param lesionId ID de la lesión a eliminar
     * @return Mono que completa cuando la operación termina
     */
    Mono<Void> removeLesion(String odontogramId, String toothNumber, String lesionId);
    
    /**
     * Añade un tratamiento a un diente en un odontograma
     * @param odontogramId ID del odontograma
     * @param toothNumber Número del diente
     * @param treatmentData Datos del tratamiento a añadir
     * @return Mono que completa cuando la operación termina
     */
    Mono<Void> addTreatment(String odontogramId, String toothNumber, Object treatmentData);
    
    /**
     * Elimina un tratamiento específico de un diente en un odontograma
     * @param odontogramId ID del odontograma
     * @param toothNumber Número del diente
     * @param treatmentId ID del tratamiento a eliminar
     * @return Mono que completa cuando la operación termina
     */
    Mono<Void> removeTreatment(String odontogramId, String toothNumber, String treatmentId);
    
    /* 
     * Métodos de compatibilidad con String
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
} 