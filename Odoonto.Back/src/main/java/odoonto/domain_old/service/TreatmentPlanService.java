package odoonto.domain.service;

import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.aggregates.Patient;

import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.TreatmentType;
import odoonto.domain.exceptions.DomainException;


import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;


/**
 * Servicio de dominio para planificar tratamientos dentales.
 * Encapsula lógica de negocio relacionada con la creación de
 * planes de tratamiento basados en el odontograma del paciente.
 */
public class TreatmentPlanService {

    // Mapa de correspondencia entre tipos de lesiones y tratamientos recomendados
    private static final Map<LesionType, TreatmentType> RECOMMENDED_TREATMENTS = createTreatmentMap();
    
    private static Map<LesionType, TreatmentType> createTreatmentMap() {
        Map<LesionType, TreatmentType> map = new HashMap<>();
        map.put(LesionType.CARIES, TreatmentType.OBTURACION_RESINA);
        map.put(LesionType.FRACTURA, TreatmentType.RECONSTRUCCION);
        map.put(LesionType.DESGASTE, TreatmentType.OBTURACION_RESINA);
        map.put(LesionType.AUSENTE, TreatmentType.IMPLANTE);
        map.put(LesionType.ENDODONCIA, TreatmentType.ENDODONCIA_UNIRRADICULAR);
        return map;
    }
    
    /**
     * Genera un plan de tratamiento basado en el odontograma del paciente
     * @param patient Paciente para el que se genera el plan
     * @return Lista de tratamientos recomendados ordenados por prioridad
     */
    public List<Map<String, Object>> generateTreatmentPlan(Patient patient) {
        if (patient == null) {
            throw new DomainException("No se puede generar un plan de tratamiento sin paciente");
        }
        
        Odontogram odontogram = patient.getOdontogram();
        if (odontogram == null) {
            throw new DomainException("El paciente no tiene odontograma registrado");
        }
        
        List<Map<String, Object>> treatmentPlan = new ArrayList<>();
        
        // Procesar cada diente con lesiones
        for (Map.Entry<String, Odontogram.ToothRecord> entry : odontogram.getTeeth().entrySet()) {
            String toothId = entry.getKey();
            Odontogram.ToothRecord toothRecord = entry.getValue();
            
            if (!toothRecord.getFaces().isEmpty()) {
                // Obtener la lesión más severa para tratamiento prioritario
                LesionType mostSevereLesionType = getMostSevereLesion(toothRecord.getFaces());
                
                // Determinar el tratamiento recomendado
                TreatmentType recommendedTreatment = getRecommendedTreatment(mostSevereLesionType);
                
                // Crear entrada para el plan de tratamiento
                Map<String, Object> treatmentEntry = new HashMap<>();
                treatmentEntry.put("toothNumber", toothId);
                treatmentEntry.put("lesionType", mostSevereLesionType.toString());
                treatmentEntry.put("recommendedTreatment", recommendedTreatment.toString());
                treatmentEntry.put("priority", getPriorityLevel(mostSevereLesionType));
                treatmentEntry.put("estimatedSessions", getEstimatedSessions(recommendedTreatment));
                treatmentEntry.put("notes", generateNotes(toothId, toothRecord, mostSevereLesionType));
                
                treatmentPlan.add(treatmentEntry);
            }
        }
        
        // Ordenar por prioridad (alta a baja)
        treatmentPlan.sort(Comparator.comparing(entry -> (Integer) entry.get("priority")));
        
        return treatmentPlan;
    }
    
    /**
     * Obtiene la lesión más severa de un mapa de lesiones
     * @param lesions Mapa de lesiones por cara dental
     * @return El tipo de lesión más severa
     */
    private LesionType getMostSevereLesion(Map<String, LesionType> lesions) {
        return lesions.values().stream()
                .max(Comparator.comparing(this::getSeverityLevel))
                .orElseThrow(() -> new DomainException("No se encontraron lesiones para evaluar"));
    }
    
    /**
     * Obtiene el nivel de severidad de un tipo de lesión
     * @param lesionType Tipo de lesión
     * @return Valor numérico de severidad (mayor es más severo)
     */
    private int getSeverityLevel(LesionType lesionType) {
        if (lesionType == LesionType.ENDODONCIA || lesionType == LesionType.AUSENTE) {
            return 5; // Muy severo
        } else if (lesionType == LesionType.FRACTURA) {
            return 4; // Severo
        } else if (lesionType == LesionType.CARIES) {
            return 3; // Moderado
        } else if (lesionType == LesionType.DESGASTE) {
            return 2; // Leve
        } else {
            return 1; // Mínimo
        }
    }
    
    /**
     * Obtiene el nivel de prioridad para un tipo de lesión
     * @param lesionType Tipo de lesión
     * @return Valor de prioridad (1 es más urgente, 3 es menos urgente)
     */
    private int getPriorityLevel(LesionType lesionType) {
        if (lesionType == LesionType.ENDODONCIA || lesionType == LesionType.AUSENTE || lesionType == LesionType.FRACTURA) {
            return 1; // Alta prioridad
        } else if (lesionType == LesionType.CARIES) {
            return 2; // Media prioridad
        } else {
            return 3; // Baja prioridad
        }
    }
    
    /**
     * Obtiene el tratamiento recomendado para un tipo de lesión
     * @param lesionType Tipo de lesión
     * @return Tipo de tratamiento recomendado
     */
    private TreatmentType getRecommendedTreatment(LesionType lesionType) {
        return RECOMMENDED_TREATMENTS.getOrDefault(lesionType, TreatmentType.RADIOGRAFIA);
    }
    
    /**
     * Estima el número de sesiones necesarias para un tratamiento
     * @param treatmentType Tipo de tratamiento
     * @return Número estimado de sesiones
     */
    private int getEstimatedSessions(TreatmentType treatmentType) {
        if (treatmentType == TreatmentType.ENDODONCIA_UNIRRADICULAR || 
            treatmentType == TreatmentType.ENDODONCIA_BIRRADICULAR || 
            treatmentType == TreatmentType.ENDODONCIA_MULTIRRADICULAR) {
            return 2;
        } else if (treatmentType == TreatmentType.CORONA || 
                  treatmentType == TreatmentType.RECONSTRUCCION) {
            return 2;
        } else if (treatmentType == TreatmentType.EXTRACCION_SIMPLE || 
                  treatmentType == TreatmentType.EXTRACCION_COMPLEJA) {
            return 1;
        } else if (treatmentType == TreatmentType.OBTURACION_RESINA ||
                  treatmentType == TreatmentType.OBTURACION_AMALGAMA) {
            return 1;
        } else if (treatmentType == TreatmentType.FLUORACION || 
                  treatmentType == TreatmentType.SELLADOR) {
            return 1;
        } else {
            return 1;
        }
    }
    
    /**
     * Genera notas adicionales para el tratamiento
     * @param toothId ID del diente a tratar
     * @param toothRecord Registro del diente
     * @param lesionType Tipo de lesión principal
     * @return Notas para el tratamiento
     */
    private String generateNotes(String toothId, Odontogram.ToothRecord toothRecord, LesionType lesionType) {
        StringBuilder notes = new StringBuilder();
        
        // Añadir información sobre el diente
        notes.append("Diente ").append(toothId).append(". ");
        
        // Añadir notas específicas por tipo de lesión
        if (lesionType == LesionType.ENDODONCIA) {
            notes.append("Evaluar posible compromiso pulpar. ");
        } else if (lesionType == LesionType.AUSENTE) {
            notes.append("Considerar opciones protésicas. ");
        } else if (lesionType == LesionType.FRACTURA) {
            notes.append("Evaluar extensión de la fractura. ");
        }
        
        // Añadir notas sobre lesiones adicionales en el mismo diente
        int lesionCount = toothRecord.getFaces().size();
        if (lesionCount > 1) {
            notes.append("El diente presenta ").append(lesionCount)
                 .append(" lesiones que pueden requerir tratamiento. ");
        }
        
        return notes.toString();
    }
} 