package odoonto.domain.repository;

import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.entities.Tooth;
import odoonto.domain.model.valueobjects.LesionType;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del repositorio para gestionar Odontogramas.
 * Nota: Los odontogramas se identifican por el ID del paciente al que pertenecen.
 */
public interface OdontogramRepository {
    
    /**
     * Guarda un odontograma para un paciente
     * @param patientId ID del paciente
     * @param odontogram Odontograma a guardar
     * @return Odontograma guardado
     */
    Odontogram save(String patientId, Odontogram odontogram);
    
    /**
     * Busca el odontograma de un paciente por su ID
     * @param patientId ID del paciente
     * @return Optional con el odontograma o vacío si no existe
     */
    Optional<Odontogram> findByPatientId(String patientId);
    
    /**
     * Busca la versión histórica de un odontograma
     * @param patientId ID del paciente
     * @param version Versión del odontograma (ej: "v1", "v2")
     * @return Optional con el odontograma o vacío si no existe
     */
    Optional<Odontogram> findHistoricalByPatientIdAndVersion(String patientId, String version);
    
    /**
     * Elimina el odontograma de un paciente
     * @param patientId ID del paciente
     */
    void deleteByPatientId(String patientId);
    
    /**
     * Verifica si existe un odontograma para un paciente
     * @param patientId ID del paciente
     * @return true si existe
     */
    boolean existsByPatientId(String patientId);
    
    /**
     * Actualiza un diente específico en el odontograma de un paciente
     * @param patientId ID del paciente
     * @param toothNumber Número del diente
     * @param tooth Diente actualizado
     * @return true si se actualizó correctamente
     */
    boolean updateTooth(String patientId, String toothNumber, Tooth tooth);
    
    /**
     * Busca odontogramas que tengan dientes con un tipo de lesión específico
     * @param lesionType Tipo de lesión
     * @return Lista de odontogramas
     */
    List<Odontogram> findByLesionType(LesionType lesionType);
    
    /**
     * Obtiene el historial de odontogramas de un paciente ordenados por fecha
     * @param patientId ID del paciente
     * @return Lista de odontogramas ordenados por fecha (más reciente primero)
     */
    List<Odontogram> findHistoryByPatientId(String patientId);
    
    /**
     * Crea una copia histórica del odontograma actual de un paciente
     * @param patientId ID del paciente
     * @return Nuevo odontograma histórico
     */
    Odontogram createHistoricalCopy(String patientId);
} 