package odoonto.domain.policy;

import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.entities.MedicalEntry;
import odoonto.domain.model.entities.MedicalRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Política que define reglas de negocio para la gestión de historiales médicos.
 */
public class MedicalRecordPolicy {
    
    // Constantes de política
    private static final int MAX_ENTRY_TITLE_LENGTH = 100;
    private static final int MAX_ENTRY_CONTENT_LENGTH = 2000;
    private static final int MAX_DAYS_TO_MODIFY_ENTRY = 7;
    private static final List<String> REQUIRED_ALLERGIES_CHECK = List.of(
            "Penicilina", "Látex", "Anestesia local", "Aspirina");
    private static final List<String> REQUIRED_MEDICAL_CONDITIONS_CHECK = List.of(
            "Hipertensión", "Diabetes", "Problemas cardíacos", "Embarazo");
    
    /**
     * Valida si una entrada médica cumple con las políticas
     * @param entry Entrada a validar
     * @throws DomainException Si no cumple con las reglas
     */
    public void validateMedicalEntry(MedicalEntry entry) {
        if (entry.getDescription() == null || entry.getDescription().trim().isEmpty()) {
            throw new DomainException("La descripción de la entrada médica no puede estar vacía");
        }
        
        if (entry.getDescription().length() > MAX_ENTRY_CONTENT_LENGTH) {
            throw new DomainException("La descripción de la entrada médica no puede exceder los " + 
                                      MAX_ENTRY_CONTENT_LENGTH + " caracteres");
        }
        
        // Verificar que la fecha de registro no sea en el futuro
        if (entry.getRecordedAt().isAfter(LocalDateTime.now())) {
            throw new DomainException("La fecha de la entrada médica no puede ser en el futuro");
        }
        
        if (entry.getDoctorId() == null || entry.getDoctorId().trim().isEmpty()) {
            throw new DomainException("El ID del doctor es obligatorio en la entrada médica");
        }
    }
    
    /**
     * Determina si una entrada médica puede ser añadida al historial
     * @param record Historial médico
     * @param entry Entrada a añadir
     * @return true si la entrada puede ser añadida
     */
    public boolean canAddEntry(MedicalRecord record, MedicalEntry entry) {
        if (record == null || entry == null) {
            return false;
        }
        
        try {
            validateMedicalEntry(entry);
            return true;
        } catch (DomainException e) {
            return false;
        }
    }
    
    /**
     * Determina si una entrada médica puede ser modificada según las políticas
     * @param entry Entrada a verificar
     * @return true si puede ser modificada
     */
    public boolean canModifyEntry(MedicalEntry entry) {
        if (entry.getRecordedAt() == null) {
            return false;
        }
        
        LocalDate entryDate = entry.getRecordedAt().toLocalDate();
        LocalDate today = LocalDate.now();
        Period period = Period.between(entryDate, today);
        
        return period.getDays() <= MAX_DAYS_TO_MODIFY_ENTRY;
    }
    
    /**
     * Verifica si las comprobaciones necesarias han sido realizadas en el historial
     * @param record Historial a verificar
     * @return Lista de verificaciones pendientes
     */
    public List<String> getMissingRequiredChecks(MedicalRecord record) {
        List<String> missingChecks = new ArrayList<>();
        
        // Verificar alergias
        List<String> recordedAllergies = record.getAllergies().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
                
        for (String requiredAllergy : REQUIRED_ALLERGIES_CHECK) {
            boolean found = recordedAllergies.stream()
                    .anyMatch(a -> a.contains(requiredAllergy.toLowerCase()));
                    
            if (!found && !record.isExplicitlyChecked("alergia_" + requiredAllergy.toLowerCase())) {
                missingChecks.add("Verificación de alergia a " + requiredAllergy);
            }
        }
        
        // Verificar condiciones médicas
        List<String> recordedConditions = record.getMedicalConditions().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
                
        for (String requiredCondition : REQUIRED_MEDICAL_CONDITIONS_CHECK) {
            boolean found = recordedConditions.stream()
                    .anyMatch(c -> c.contains(requiredCondition.toLowerCase()));
                    
            if (!found && !record.isExplicitlyChecked("condicion_" + requiredCondition.toLowerCase())) {
                missingChecks.add("Verificación de " + requiredCondition);
            }
        }
        
        return missingChecks;
    }
    
    /**
     * Determina si es necesario actualizar el historial médico
     * @param record Historial a verificar
     * @return true si el historial requiere actualización
     */
    public boolean requiresUpdate(MedicalRecord record) {
        if (record.getLastUpdated() == null) {
            return true;
        }
        
        LocalDate today = LocalDate.now();
        Period period = Period.between(record.getLastUpdated(), today);
        
        // Actualizar cada 6 meses
        return period.getMonths() >= 6 || period.getYears() > 0;
    }
    
    /**
     * Determina si un historial médico está completo para procedimientos críticos
     * @param record Historial a verificar
     * @return true si el historial está completo para procedimientos críticos
     */
    public boolean isCompleteForCriticalProcedures(MedicalRecord record) {
        // No debe haber verificaciones pendientes
        if (!getMissingRequiredChecks(record).isEmpty()) {
            return false;
        }
        
        // Debe estar actualizado
        if (requiresUpdate(record)) {
            return false;
        }
        
        // Debe tener al menos una entrada médica
        if (record.getEntries().isEmpty()) {
            return false;
        }
        
        return true;
    }
} 