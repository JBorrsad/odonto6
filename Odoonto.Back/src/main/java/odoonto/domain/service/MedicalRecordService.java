package odoonto.domain.service;

import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.entities.MedicalEntry;
import odoonto.domain.model.entities.MedicalRecord;
import odoonto.domain.policy.MedicalRecordPolicy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de dominio para gestión de historiales médicos.
 */
public class MedicalRecordService {
    
    private final MedicalRecordPolicy policy;
    
    /**
     * Constructor con inyección de política
     * @param policy Política de historiales médicos
     */
    public MedicalRecordService(MedicalRecordPolicy policy) {
        this.policy = policy;
    }
    
    /**
     * Añade una entrada al historial médico validando según las políticas
     * @param record Historial médico
     * @param entry Entrada a añadir
     * @return Historial médico actualizado
     */
    public MedicalRecord addEntry(MedicalRecord record, MedicalEntry entry) {
        if (record == null) {
            throw new DomainException("El historial médico no puede ser nulo");
        }
        
        if (entry == null) {
            throw new DomainException("La entrada médica no puede ser nula");
        }
        
        // Validar según políticas
        if (!policy.canAddEntry(record, entry)) {
            throw new DomainException("No se puede añadir la entrada debido a restricciones de política");
        }
        
        record.addEntry(entry);
        return record;
    }
    
    /**
     * Obtiene las entradas médicas dentro de un rango de fechas
     * @param record Historial médico
     * @param from Fecha inicial (inclusive)
     * @param to Fecha final (inclusive)
     * @return Lista de entradas dentro del rango
     */
    public List<MedicalEntry> getEntriesInDateRange(MedicalRecord record, LocalDate from, LocalDate to) {
        if (record == null) {
            return new ArrayList<>();
        }
        
        List<MedicalEntry> result = new ArrayList<>();
        for (MedicalEntry entry : record.getEntries()) {
            LocalDate entryDate = entry.getRecordedAt().toLocalDate();
            if ((from == null || !entryDate.isBefore(from)) && 
                (to == null || !entryDate.isAfter(to))) {
                result.add(entry);
            }
        }
        
        return result;
    }
    
    /**
     * Busca entradas médicas que contengan el texto especificado
     * @param record Historial médico
     * @param searchText Texto a buscar
     * @return Lista de entradas que contienen el texto
     */
    public List<MedicalEntry> searchEntries(MedicalRecord record, String searchText) {
        if (record == null || searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerCaseSearch = searchText.toLowerCase();
        List<MedicalEntry> results = new ArrayList<>();
        
        for (MedicalEntry entry : record.getEntries()) {
            boolean matches = false;
            
            // Buscar en diferentes campos
            if (entry.getDescription() != null && 
                entry.getDescription().toLowerCase().contains(lowerCaseSearch)) {
                matches = true;
            }
            
            if (entry.getNotes() != null && 
                entry.getNotes().toLowerCase().contains(lowerCaseSearch)) {
                matches = true;
            }
            
            if (entry.getType() != null && 
                entry.getType().toLowerCase().contains(lowerCaseSearch)) {
                matches = true;
            }
            
            if (matches) {
                results.add(entry);
            }
        }
        
        return results;
    }
    
    /**
     * Verifica si el paciente tiene alguna alergia específica
     * @param record Historial médico
     * @param allergyName Nombre de la alergia
     * @return true si el paciente tiene la alergia registrada
     */
    public boolean hasAllergy(MedicalRecord record, String allergyName) {
        if (record == null || allergyName == null || allergyName.trim().isEmpty()) {
            return false;
        }
        
        String lowercaseAllergy = allergyName.toLowerCase();
        for (String allergy : record.getAllergies()) {
            if (allergy.toLowerCase().contains(lowercaseAllergy)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Genera un resumen del historial médico
     * @param record Historial médico
     * @return Resumen del historial
     */
    public MedicalRecordSummary generateSummary(MedicalRecord record) {
        if (record == null) {
            throw new DomainException("El historial médico no puede ser nulo");
        }
        
        MedicalRecordSummary summary = new MedicalRecordSummary();
        
        // Datos básicos
        summary.setPatientId(record.extractPatientId() != null ? record.extractPatientId().getValue() : null);
        summary.setTotalEntries(record.getEntries().size());
        summary.setAllergies(new ArrayList<>(record.getAllergies()));
        summary.setMedicalConditions(new ArrayList<>(record.getMedicalConditions()));
        
        // Fecha de primera y última visita
        if (!record.getEntries().isEmpty()) {
            summary.setFirstVisitDate(findFirstVisitDate(record));
            summary.setLastVisitDate(findLastVisitDate(record));
        }
        
        // Categorizar entradas por tipo
        int diagnosisCount = 0;
        int treatmentCount = 0;
        int controlCount = 0;
        
        for (MedicalEntry entry : record.getEntries()) {
            // Contar entradas de diagnóstico
            if (entry.getType() != null && entry.getType().equalsIgnoreCase("diagnostico")) {
                diagnosisCount++;
            }
            
            // Contar entradas de tratamiento
            if (entry.getType() != null && entry.getType().equalsIgnoreCase("tratamiento")) {
                treatmentCount++;
            }
            
            // Contar entradas de control
            if (entry.getType() != null && entry.getType().equalsIgnoreCase("control")) {
                controlCount++;
            }
        }
        
        summary.setDiagnosisCount(diagnosisCount);
        summary.setTreatmentCount(treatmentCount);
        summary.setControlCount(controlCount);
        
        return summary;
    }
    
    /**
     * Encuentra la fecha de la primera visita
     * @param record Historial médico
     * @return Fecha de la primera visita
     */
    private LocalDate findFirstVisitDate(MedicalRecord record) {
        return record.getEntries().stream()
                .map(entry -> entry.getRecordedAt().toLocalDate())
                .min(LocalDate::compareTo)
                .orElse(null);
    }
    
    /**
     * Encuentra la fecha de la última visita
     * @param record Historial médico
     * @return Fecha de la última visita
     */
    private LocalDate findLastVisitDate(MedicalRecord record) {
        return record.getEntries().stream()
                .map(entry -> entry.getRecordedAt().toLocalDate())
                .max(LocalDate::compareTo)
                .orElse(null);
    }
    
    /**
     * Clase que representa un resumen del historial médico
     */
    public static class MedicalRecordSummary {
        private String patientId;
        private int totalEntries;
        private LocalDate firstVisitDate;
        private LocalDate lastVisitDate;
        private List<String> allergies = new ArrayList<>();
        private List<String> medicalConditions = new ArrayList<>();
        private int diagnosisCount;
        private int treatmentCount;
        private int controlCount;
        
        // Getters y setters
        public String getPatientId() { return patientId; }
        public void setPatientId(String patientId) { this.patientId = patientId; }
        
        public int getTotalEntries() { return totalEntries; }
        public void setTotalEntries(int totalEntries) { this.totalEntries = totalEntries; }
        
        public LocalDate getFirstVisitDate() { return firstVisitDate; }
        public void setFirstVisitDate(LocalDate firstVisitDate) { this.firstVisitDate = firstVisitDate; }
        
        public LocalDate getLastVisitDate() { return lastVisitDate; }
        public void setLastVisitDate(LocalDate lastVisitDate) { this.lastVisitDate = lastVisitDate; }
        
        public List<String> getAllergies() { return allergies; }
        public void setAllergies(List<String> allergies) { this.allergies = allergies; }
        
        public List<String> getMedicalConditions() { return medicalConditions; }
        public void setMedicalConditions(List<String> conditions) { this.medicalConditions = conditions; }
        
        public int getDiagnosisCount() { return diagnosisCount; }
        public void setDiagnosisCount(int count) { this.diagnosisCount = count; }
        
        public int getTreatmentCount() { return treatmentCount; }
        public void setTreatmentCount(int count) { this.treatmentCount = count; }
        
        public int getControlCount() { return controlCount; }
        public void setControlCount(int count) { this.controlCount = count; }
    }
} 