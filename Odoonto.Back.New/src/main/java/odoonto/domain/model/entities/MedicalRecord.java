package odoonto.domain.model.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa el historial médico de un paciente.
 * Contiene una colección de entradas médicas ordenadas cronológicamente.
 */
public class MedicalRecord {
    private final String patientId;
    private final LocalDateTime createdAt;
    private final List<MedicalEntry> entries;
    
    /**
     * Constructor para un historial médico
     * @param patientId ID del paciente dueño del historial
     */
    public MedicalRecord(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del paciente no puede estar vacío");
        }
        
        this.patientId = patientId;
        this.createdAt = LocalDateTime.now();
        this.entries = new ArrayList<>();
    }
    
    /**
     * Añade una nueva entrada al historial médico
     * @param entry Entrada médica a añadir
     */
    public void addEntry(MedicalEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("La entrada no puede ser nula");
        }
        
        entries.add(entry);
    }
    
    /**
     * Obtiene todas las entradas en orden cronológico
     * @return Lista inmutable de entradas
     */
    public List<MedicalEntry> getEntries() {
        // Ordenar por fecha, la más reciente primero
        entries.sort((e1, e2) -> e2.getRecordedAt().compareTo(e1.getRecordedAt()));
        return Collections.unmodifiableList(entries);
    }
    
    /**
     * Obtiene las entradas filtradas por un tipo específico
     * @param entryType Tipo de entrada a filtrar
     * @return Lista de entradas del tipo especificado
     */
    public List<MedicalEntry> getEntriesByType(String entryType) {
        if (entryType == null || entryType.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        List<MedicalEntry> filteredEntries = new ArrayList<>();
        for (MedicalEntry entry : entries) {
            if (entryType.equals(entry.getType())) {
                filteredEntries.add(entry);
            }
        }
        
        // Ordenar por fecha, la más reciente primero
        filteredEntries.sort((e1, e2) -> e2.getRecordedAt().compareTo(e1.getRecordedAt()));
        return Collections.unmodifiableList(filteredEntries);
    }
    
    /**
     * Obtiene el ID del paciente
     * @return ID del paciente
     */
    public String getPatientId() {
        return patientId;
    }
    
    /**
     * Obtiene la fecha de creación del historial
     * @return Fecha de creación
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Obtiene el número total de entradas
     * @return Número de entradas
     */
    public int getTotalEntries() {
        return entries.size();
    }
} 