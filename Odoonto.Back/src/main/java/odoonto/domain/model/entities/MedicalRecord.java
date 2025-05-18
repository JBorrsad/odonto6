package odoonto.domain.model.entities;

import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.valueobjects.MedicalRecordId;
import odoonto.domain.model.valueobjects.PatientId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa el historial médico de un paciente.
 * Es un agregado independiente relacionado con Patient mediante identidad derivada.
 * Contiene una colección de entradas médicas ordenadas cronológicamente.
 */
public class MedicalRecord {
    private MedicalRecordId id;
    private final LocalDateTime createdAt;
    private final List<MedicalEntry> entries;
    private LocalDate lastUpdated;
    private List<String> allergies;
    private List<String> medicalConditions;
    private Map<String, Boolean> explicitChecks;
    
    /**
     * Constructor por defecto para frameworks
     */
    public MedicalRecord() {
        this.createdAt = LocalDateTime.now();
        this.entries = new ArrayList<>();
        this.lastUpdated = LocalDate.now();
        this.allergies = new ArrayList<>();
        this.medicalConditions = new ArrayList<>();
        this.explicitChecks = new HashMap<>();
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
        this.lastUpdated = LocalDate.now();
        this.allergies = new ArrayList<>();
        this.medicalConditions = new ArrayList<>();
        this.explicitChecks = new HashMap<>();
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
        this.lastUpdated = LocalDate.now();
        this.allergies = new ArrayList<>();
        this.medicalConditions = new ArrayList<>();
        this.explicitChecks = new HashMap<>();
    }
    
    /**
     * Constructor completo para reconstrucción desde persistencia con todos los campos
     * @param id ID del historial médico
     * @param createdAt Fecha de creación
     * @param entries Lista de entradas médicas
     * @param lastUpdated Fecha de última actualización
     * @param allergies Lista de alergias
     * @param medicalConditions Lista de condiciones médicas
     * @param explicitChecks Mapa de verificaciones explícitas
     */
    public MedicalRecord(MedicalRecordId id, LocalDateTime createdAt, List<MedicalEntry> entries,
                         LocalDate lastUpdated, List<String> allergies, List<String> medicalConditions,
                         Map<String, Boolean> explicitChecks) {
        if (id == null) {
            throw new DomainException("El ID del historial médico no puede ser nulo");
        }
        
        this.id = id;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.entries = entries != null ? new ArrayList<>(entries) : new ArrayList<>();
        this.lastUpdated = lastUpdated != null ? lastUpdated : LocalDate.now();
        this.allergies = allergies != null ? new ArrayList<>(allergies) : new ArrayList<>();
        this.medicalConditions = medicalConditions != null ? new ArrayList<>(medicalConditions) : new ArrayList<>();
        this.explicitChecks = explicitChecks != null ? new HashMap<>(explicitChecks) : new HashMap<>();
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
        this.lastUpdated = LocalDate.now();
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
    
    /**
     * Obtiene la fecha de última actualización del historial
     * @return Fecha de última actualización
     */
    public LocalDate getLastUpdated() {
        return lastUpdated;
    }
    
    /**
     * Establece la fecha de última actualización
     * @param lastUpdated Nueva fecha de última actualización
     */
    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    /**
     * Obtiene la lista de alergias del paciente
     * @return Lista inmutable de alergias
     */
    public List<String> getAllergies() {
        return Collections.unmodifiableList(allergies);
    }
    
    /**
     * Establece la lista de alergias del paciente
     * @param allergies Nueva lista de alergias
     */
    public void setAllergies(List<String> allergies) {
        this.allergies = allergies != null ? new ArrayList<>(allergies) : new ArrayList<>();
        this.lastUpdated = LocalDate.now();
    }
    
    /**
     * Añade una alergia a la lista
     * @param allergy Alergia a añadir
     */
    public void addAllergy(String allergy) {
        if (allergy != null && !allergy.trim().isEmpty()) {
            this.allergies.add(allergy);
            this.lastUpdated = LocalDate.now();
        }
    }
    
    /**
     * Obtiene la lista de condiciones médicas del paciente
     * @return Lista inmutable de condiciones médicas
     */
    public List<String> getMedicalConditions() {
        return Collections.unmodifiableList(medicalConditions);
    }
    
    /**
     * Establece la lista de condiciones médicas del paciente
     * @param medicalConditions Nueva lista de condiciones médicas
     */
    public void setMedicalConditions(List<String> medicalConditions) {
        this.medicalConditions = medicalConditions != null ? new ArrayList<>(medicalConditions) : new ArrayList<>();
        this.lastUpdated = LocalDate.now();
    }
    
    /**
     * Añade una condición médica a la lista
     * @param condition Condición médica a añadir
     */
    public void addMedicalCondition(String condition) {
        if (condition != null && !condition.trim().isEmpty()) {
            this.medicalConditions.add(condition);
            this.lastUpdated = LocalDate.now();
        }
    }
    
    /**
     * Verifica si una comprobación específica ha sido marcada explícitamente
     * @param checkCode Código de la comprobación
     * @return true si la comprobación ha sido marcada explícitamente
     */
    public boolean isExplicitlyChecked(String checkCode) {
        return explicitChecks.getOrDefault(checkCode, false);
    }
    
    /**
     * Marca una comprobación como verificada explícitamente
     * @param checkCode Código de la comprobación
     * @param checked Estado de la comprobación
     */
    public void setExplicitlyChecked(String checkCode, boolean checked) {
        if (checkCode != null && !checkCode.trim().isEmpty()) {
            explicitChecks.put(checkCode, checked);
            this.lastUpdated = LocalDate.now();
        }
    }
    
    /**
     * Obtiene el mapa completo de comprobaciones explícitas
     * @return Mapa inmutable de comprobaciones explícitas
     */
    public Map<String, Boolean> getExplicitChecks() {
        return Collections.unmodifiableMap(explicitChecks);
    }
} 