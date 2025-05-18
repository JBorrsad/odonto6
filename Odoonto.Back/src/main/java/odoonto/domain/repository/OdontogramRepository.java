package odoonto.domain.repository;

import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.entities.Tooth;
import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.OdontogramId;
import odoonto.domain.model.valueobjects.PatientId;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del repositorio para gestionar Odontogramas.
 * Define operaciones puras de dominio sin dependencias externas.
 * Nota: Los odontogramas se identifican por el ID del paciente al que pertenecen.
 */
public interface OdontogramRepository {
    
    /**
     * Busca todos los odontogramas
     * @return Lista con todos los odontogramas
     */
    List<Odontogram> findAll();
    
    /**
     * Busca un odontograma por su identificador
     * @param id Identificador único del odontograma
     * @return Optional con el odontograma encontrado o empty si no existe
     */
    Optional<Odontogram> findById(OdontogramId id);
    
    /**
     * Busca un odontograma por el identificador del paciente
     * @param patientId Identificador único del paciente
     * @return Optional con el odontograma encontrado o empty si no existe
     */
    Optional<Odontogram> findByPatientId(PatientId patientId);
    
    /**
     * Guarda un odontograma en el repositorio
     * @param odontogram Odontograma a guardar
     * @return El odontograma guardado
     */
    Odontogram save(Odontogram odontogram);
    
    /**
     * Elimina un odontograma por su identificador
     * @param id Identificador único del odontograma
     */
    void deleteById(OdontogramId id);
    
    /**
     * Busca odontogramas que contienen un tipo específico de lesión
     * @param lesionType Tipo de lesión a buscar
     * @return Lista con los odontogramas que contienen el tipo de lesión especificado
     */
    List<Odontogram> findByLesionType(LesionType lesionType);
    
    /**
     * Verifica si existe un odontograma para el paciente especificado
     * @param patientId Identificador único del paciente
     * @return true si existe o false si no existe
     */
    boolean existsByPatientId(PatientId patientId);
    
    /**
     * Elimina un odontograma por el identificador del paciente
     * @param patientId Identificador único del paciente
     */
    void deleteByPatientId(PatientId patientId);
    
    /**
     * Crea una copia histórica de un odontograma
     * @param odontogramId Identificador único del odontograma a copiar
     * @return ID de la versión histórica creada
     */
    String createHistoricalCopy(OdontogramId odontogramId);
    
    /**
     * Busca todas las versiones históricas de un odontograma por el identificador del paciente
     * @param patientId Identificador único del paciente
     * @return Lista con las versiones históricas del odontograma
     */
    List<Odontogram> findHistoryByPatientId(PatientId patientId);
    
    /**
     * Busca una versión histórica específica de un odontograma
     * @param patientId Identificador único del paciente
     * @param version Identificador de versión
     * @return Optional con la versión histórica del odontograma o empty si no existe
     */
    Optional<Odontogram> findHistoricalByPatientIdAndVersion(PatientId patientId, String version);
    
    /**
     * Actualiza un diente específico en el odontograma de un paciente
     * @param patientId ID del paciente
     * @param toothNumber Número del diente
     * @param tooth Diente actualizado
     * @return true si se actualizó correctamente
     */
    boolean updateTooth(String patientId, String toothNumber, Tooth tooth);
    
    /**
     * Elimina una lesión específica de un diente en un odontograma por ID
     * @param odontogramId ID del odontograma
     * @param toothNumber Número del diente
     * @param lesionId ID de la lesión a eliminar
     */
    void removeLesion(String odontogramId, String toothNumber, String lesionId);
    
    /**
     * Añade un tratamiento a un diente en un odontograma
     * @param odontogramId ID del odontograma
     * @param toothNumber Número del diente
     * @param treatmentData Datos del tratamiento a añadir
     */
    void addTreatment(String odontogramId, String toothNumber, Object treatmentData);
    
    /**
     * Elimina un tratamiento específico de un diente en un odontograma
     * @param odontogramId ID del odontograma
     * @param toothNumber Número del diente
     * @param treatmentId ID del tratamiento a eliminar
     */
    void removeTreatment(String odontogramId, String toothNumber, String treatmentId);
    
    /* 
     * Métodos de compatibilidad para mantener retrocompatibilidad
     */
     
    /**
     * Método de compatibilidad para buscar por id usando String
     * @param id Identificador como String
     * @return Optional con el odontograma encontrado o empty si no existe
     */
    default Optional<Odontogram> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        return findById(OdontogramId.of(id));
    }
    
    /**
     * Método de compatibilidad para buscar por patientId usando String
     * @param patientId Identificador del paciente como String
     * @return Optional con el odontograma encontrado o empty si no existe
     */
    default Optional<Odontogram> findByPatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return Optional.empty();
        }
        return findByPatientId(PatientId.of(patientId));
    }
    
    /**
     * Método de compatibilidad para eliminar por id usando String
     * @param id Identificador como String
     */
    default void deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return;
        }
        deleteById(OdontogramId.of(id));
    }
    
    /**
     * Método de compatibilidad para verificar existencia por patientId usando String
     * @param patientId Identificador del paciente como String
     * @return true si existe o false si no existe
     */
    default boolean existsByPatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return false;
        }
        return existsByPatientId(PatientId.of(patientId));
    }
    
    /**
     * Método de compatibilidad para eliminar por patientId usando String
     * @param patientId Identificador del paciente como String
     */
    default void deleteByPatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return;
        }
        deleteByPatientId(PatientId.of(patientId));
    }
    
    /**
     * Método de compatibilidad para crear copia histórica usando String
     * @param odontogramId Identificador del odontograma como String
     * @return ID de la versión histórica creada
     */
    default String createHistoricalCopy(String odontogramId) {
        if (odontogramId == null || odontogramId.trim().isEmpty()) {
            return null;
        }
        return createHistoricalCopy(OdontogramId.of(odontogramId));
    }
    
    /**
     * Método de compatibilidad para buscar historia por patientId usando String
     * @param patientId Identificador del paciente como String
     * @return Lista con las versiones históricas del odontograma
     */
    default List<Odontogram> findHistoryByPatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return List.of();
        }
        return findHistoryByPatientId(PatientId.of(patientId));
    }
    
    /**
     * Método de compatibilidad para buscar versión histórica usando String
     * @param patientId Identificador del paciente como String
     * @param version Identificador de versión
     * @return Optional con la versión histórica del odontograma o empty si no existe
     */
    default Optional<Odontogram> findHistoricalByPatientIdAndVersion(String patientId, String version) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return Optional.empty();
        }
        return findHistoricalByPatientIdAndVersion(PatientId.of(patientId), version);
    }
} 