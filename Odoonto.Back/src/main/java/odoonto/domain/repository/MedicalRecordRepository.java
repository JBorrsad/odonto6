package odoonto.domain.repository;

import odoonto.domain.model.entities.MedicalEntry;
import odoonto.domain.model.entities.MedicalRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz del repositorio para gestionar Historiales Médicos.
 */
public interface MedicalRecordRepository {
    
    /**
     * Guarda un historial médico
     * @param medicalRecord Historial médico a guardar
     * @return Historial médico guardado con su ID
     */
    MedicalRecord save(MedicalRecord medicalRecord);
    
    /**
     * Busca un historial médico por su ID
     * @param id ID del historial médico
     * @return Optional con el historial médico o vacío si no existe
     */
    Optional<MedicalRecord> findById(String id);
    
    /**
     * Busca el historial médico de un paciente
     * @param patientId ID del paciente
     * @return Optional con el historial médico o vacío si no existe
     */
    Optional<MedicalRecord> findByPatientId(String patientId);
    
    /**
     * Elimina un historial médico
     * @param id ID del historial médico
     */
    void deleteById(String id);
    
    /**
     * Verifica si existe un historial médico
     * @param id ID del historial médico
     * @return true si existe
     */
    boolean existsById(String id);
    
    /**
     * Añade una entrada al historial médico
     * @param medicalRecordId ID del historial médico
     * @param entry Entrada a añadir
     * @return Entrada añadida con su ID
     */
    MedicalEntry addEntry(String medicalRecordId, MedicalEntry entry);
    
    /**
     * Obtiene todas las entradas de un historial médico
     * @param medicalRecordId ID del historial médico
     * @return Lista de entradas
     */
    List<MedicalEntry> findAllEntries(String medicalRecordId);
    
    /**
     * Busca entradas por fecha
     * @param medicalRecordId ID del historial médico
     * @param date Fecha a buscar
     * @return Lista de entradas en esa fecha
     */
    List<MedicalEntry> findEntriesByDate(String medicalRecordId, LocalDate date);
    
    /**
     * Busca entradas por doctor
     * @param medicalRecordId ID del historial médico
     * @param doctorId ID del doctor
     * @return Lista de entradas realizadas por ese doctor
     */
    List<MedicalEntry> findEntriesByDoctor(String medicalRecordId, String doctorId);
    
    /**
     * Actualiza una entrada específica
     * @param medicalRecordId ID del historial médico
     * @param entryId ID de la entrada
     * @param entry Entrada actualizada
     * @return true si se actualizó correctamente
     */
    boolean updateEntry(String medicalRecordId, String entryId, MedicalEntry entry);
    
    /**
     * Elimina una entrada
     * @param medicalRecordId ID del historial médico
     * @param entryId ID de la entrada
     * @return true si se eliminó correctamente
     */
    boolean deleteEntry(String medicalRecordId, String entryId);
    
    /**
     * Añade una alergia al historial médico
     * @param medicalRecordId ID del historial médico
     * @param allergy Alergia a añadir
     * @return true si se añadió correctamente
     */
    boolean addAllergy(String medicalRecordId, String allergy);
    
    /**
     * Añade una condición médica al historial
     * @param medicalRecordId ID del historial médico
     * @param condition Condición médica a añadir
     * @return true si se añadió correctamente
     */
    boolean addMedicalCondition(String medicalRecordId, String condition);
    
    /**
     * Busca historiales médicos que contengan una alergia específica
     * @param allergy Alergia a buscar
     * @return Lista de historiales médicos
     */
    List<MedicalRecord> findByAllergy(String allergy);
    
    /**
     * Busca historiales médicos que contengan una condición médica específica
     * @param condition Condición médica a buscar
     * @return Lista de historiales médicos
     */
    List<MedicalRecord> findByMedicalCondition(String condition);
} 