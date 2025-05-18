package odoonto.domain.service;

import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.model.entities.Tooth;
import odoonto.domain.model.entities.Lesion;
import odoonto.domain.model.entities.Treatment;
import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.TreatmentType;
import odoonto.domain.exceptions.DomainException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

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
        map.put(LesionType.CARIES_INCIPIENTE, TreatmentType.APLICACION_FLUOR);
        map.put(LesionType.CARIES_MODERADA, TreatmentType.OBTURACION_RESINA);
        map.put(LesionType.CARIES_AVANZADA, TreatmentType.ENDODONCIA);
        map.put(LesionType.MANCHA_BLANCA, TreatmentType.APLICACION_FLUOR);
        map.put(LesionType.FRACTURA, TreatmentType.RECONSTRUCCION);
        map.put(LesionType.ABSCESO, TreatmentType.ENDODONCIA);
        map.put(LesionType.EROSION, TreatmentType.OBTURACION_RESINA);
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
        for (Tooth tooth : odontogram.getTeeth()) {
            if (!tooth.getLesions().isEmpty()) {
                // Obtener la lesión más severa para tratamiento prioritario
                Lesion mostSevereLesion = getMostSevereLesion(tooth.getLesions());
                
                // Determinar el tratamiento recomendado
                TreatmentType recommendedTreatment = getRecommendedTreatment(mostSevereLesion.getType());
                
                // Crear entrada para el plan de tratamiento
                Map<String, Object> treatmentEntry = new HashMap<>();
                treatmentEntry.put("toothNumber", tooth.getNumber());
                treatmentEntry.put("lesionType", mostSevereLesion.getType().toString());
                treatmentEntry.put("recommendedTreatment", recommendedTreatment.toString());
                treatmentEntry.put("priority", getPriorityLevel(mostSevereLesion.getType()));
                treatmentEntry.put("estimatedSessions", getEstimatedSessions(recommendedTreatment));
                treatmentEntry.put("notes", generateNotes(tooth, mostSevereLesion));
                
                treatmentPlan.add(treatmentEntry);
            }
        }
        
        // Ordenar por prioridad (alta a baja)
        treatmentPlan.sort(Comparator.comparing(entry -> (Integer) entry.get("priority")));
        
        return treatmentPlan;
    }
    
    /**
     * Obtiene la lesión más severa de una lista de lesiones
     * @param lesions Lista de lesiones a evaluar
     * @return La lesión más severa
     */
    private Lesion getMostSevereLesion(List<Lesion> lesions) {
        return lesions.stream()
                .max(Comparator.comparing(lesion -> getSeverityLevel(lesion.getType())))
                .orElseThrow(() -> new DomainException("No se encontraron lesiones para evaluar"));
    }
    
    /**
     * Obtiene el nivel de severidad de un tipo de lesión
     * @param lesionType Tipo de lesión
     * @return Valor numérico de severidad (mayor es más severo)
     */
    private int getSeverityLevel(LesionType lesionType) {
        switch (lesionType) {
            case CARIES_AVANZADA:
            case ABSCESO:
                return 5;
            case FRACTURA:
                return 4;
            case CARIES_MODERADA:
                return 3;
            case CARIES_INCIPIENTE:
            case EROSION:
                return 2;
            case MANCHA_BLANCA:
            default:
                return 1;
        }
    }
    
    /**
     * Obtiene el nivel de prioridad para un tipo de lesión
     * @param lesionType Tipo de lesión
     * @return Valor de prioridad (1 es más urgente, 3 es menos urgente)
     */
    private int getPriorityLevel(LesionType lesionType) {
        switch (lesionType) {
            case CARIES_AVANZADA:
            case ABSCESO:
            case FRACTURA:
                return 1; // Alta prioridad
            case CARIES_MODERADA:
            case EROSION:
                return 2; // Media prioridad
            case CARIES_INCIPIENTE:
            case MANCHA_BLANCA:
            default:
                return 3; // Baja prioridad
        }
    }
    
    /**
     * Obtiene el tratamiento recomendado para un tipo de lesión
     * @param lesionType Tipo de lesión
     * @return Tipo de tratamiento recomendado
     */
    private TreatmentType getRecommendedTreatment(LesionType lesionType) {
        return RECOMMENDED_TREATMENTS.getOrDefault(lesionType, TreatmentType.EVALUACION);
    }
    
    /**
     * Estima el número de sesiones necesarias para un tratamiento
     * @param treatmentType Tipo de tratamiento
     * @return Número estimado de sesiones
     */
    private int getEstimatedSessions(TreatmentType treatmentType) {
        switch (treatmentType) {
            case ENDODONCIA:
                return 2;
            case CORONA:
            case RECONSTRUCCION:
                return 2;
            case EXTRACCION:
                return 1;
            case OBTURACION_RESINA:
            case OBTURACION_AMALGAMA:
                return 1;
            case APLICACION_FLUOR:
            case SELLANTE:
                return 1;
            default:
                return 1;
        }
    }
    
    /**
     * Genera notas adicionales para el tratamiento
     * @param tooth Diente a tratar
     * @param lesion Lesión principal
     * @return Notas para el tratamiento
     */
    private String generateNotes(Tooth tooth, Lesion lesion) {
        StringBuilder notes = new StringBuilder();
        
        // Añadir información sobre la ubicación de la lesión
        notes.append("Lesión en ").append(lesion.getFace().toString().toLowerCase()).append(". ");
        
        // Añadir notas específicas por tipo de lesión
        switch (lesion.getType()) {
            case CARIES_AVANZADA:
                notes.append("Evaluar posible compromiso pulpar. ");
                break;
            case ABSCESO:
                notes.append("Considerar medicación antibiótica previa. ");
                break;
            case FRACTURA:
                notes.append("Evaluar extensión de la fractura. ");
                break;
            default:
                // No se añaden notas adicionales
                break;
        }
        
        // Añadir notas sobre lesiones adicionales en el mismo diente
        int additionalLesions = tooth.getLesions().size() - 1;
        if (additionalLesions > 0) {
            notes.append("El diente presenta ").append(additionalLesions)
                 .append(" lesión(es) adicional(es) que pueden requerir tratamiento. ");
        }
        
        return notes.toString();
    }
} 