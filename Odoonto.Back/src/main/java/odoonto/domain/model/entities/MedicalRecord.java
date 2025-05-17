package odoonto.domain.model.entities;

import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.valueobjects.MedicalRecordId;
import odoonto.domain.model.valueobjects.PatientId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa el historial médico de un paciente.
 * Es un agregado independiente relacionado con Patient mediante identidad derivada.
 * Contiene una colección de entradas médicas ordenadas cronológicamente.
 */
public class MedicalRecord {
    private MedicalRecordId id;
    private final LocalDateTime createdAt;
    private final List<MedicalEntry> entries;
    
    /**
     * Constructor por defecto para frameworks
     */
    public MedicalRecord() {
        this.createdAt = LocalDateTime.now();
        this.entries = new ArrayList<>();
    }
    
    /**
     * Constructor para un historial médico basado en el ID del paciente
     * @param patientId ID del paciente dueño del historial
     */
    public MedicalRecord(PatientId patientId) {
        if (patientId == null) {
            throw new DomainException("El ID del paciente no puede ser nulo");
        }
        
        this.id = MedicalRecordId.fromPatientId(patientId);
        this.createdAt = LocalDateTime.now();
        this.entries = new ArrayList<>();
    }
    
    /**
     * Constructor completo para reconstrucción desde persistencia
     * @param id ID del historial médico
     * @param createdAt Fecha de creación
     * @param entries Lista de entradas médicas
     */
    public MedicalRecord(MedicalRecordId id, LocalDateTime createdAt, List<MedicalEntry> entries) {
        if (id == null) {
            throw new DomainException("El ID del historial médico no puede ser nulo");
        }
        
        this.id = id;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.entries = entries != null ? new ArrayList<>(entries) : new ArrayList<>();
    }
    
    /**
     * Añade una nueva entrada al historial médico
     * @param entry Entrada médica a añadir
     */
    public void addEntry(MedicalEntry entry) {
        if (entry == null) {
            throw new DomainException("La entrada no puede ser nula");
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
     * Obtiene el ID del historial médico
     * @return ID del historial médico
     */
    public MedicalRecordId getId() {
        return id;
    }
    
    /**
     * Establece el ID del historial médico
     * @param id Nuevo ID
     */
    public void setId(MedicalRecordId id) {
        this.id = id;
    }
    
    /**
     * Extrae el ID del paciente asociado con este historial médico
     * @return ID del paciente o null si el ID no es derivado
     */
    public PatientId extractPatientId() {
        return id != null ? id.extractPatientId() : null;
    }
    
    /**
     * Obtiene el valor del ID como String
     * @return Valor del ID como String o null si es nulo
     */
    public String getIdValue() {
        return id != null ? id.getValue() : null;
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