package odoonto.domain.service;

import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.entities.Lesion;
import odoonto.domain.model.entities.Tooth;
import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.ToothFace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio de dominio para diagnóstico y análisis de lesiones dentales.
 * Trabaja con el odontograma, que es un objeto de valor dentro del agregado Patient.
 */
public class DentalDiagnosisService {
    
    /**
     * Identifica los dientes que requieren atención inmediata en un odontograma
     * @param odontogram Odontograma a analizar (objeto de valor del paciente)
     * @return Lista de dientes que requieren atención inmediata
     */
    public List<Tooth> identifyPriorityTeeth(Odontogram odontogram) {
        List<Tooth> priorityTeeth = new ArrayList<>();
        
        for (Tooth tooth : odontogram.getTeeth()) {
            for (Lesion lesion : tooth.getLesions()) {
                if (requiresImmediateAttention(lesion.getType())) {
                    priorityTeeth.add(tooth);
                    break;
                }
            }
        }
        
        return priorityTeeth;
    }
    
    /**
     * Determina si un tipo de lesión requiere atención inmediata
     * @param lesionType Tipo de lesión
     * @return true si requiere atención inmediata
     */
    private boolean requiresImmediateAttention(LesionType lesionType) {
        switch (lesionType) {
            case CARIES_PROFUNDA:
            case PULPITIS:
            case ABSCESO:
            case FRACTURA:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Genera un resumen de lesiones por tipo
     * @param odontogram Odontograma a analizar
     * @return Mapa de tipo de lesión a cantidad
     */
    public Map<LesionType, Integer> summarizeLesionsByType(Odontogram odontogram) {
        Map<LesionType, Integer> summary = new HashMap<>();
        
        for (LesionType type : LesionType.values()) {
            summary.put(type, 0);
        }
        
        for (Tooth tooth : odontogram.getTeeth()) {
            for (Lesion lesion : tooth.getLesions()) {
                LesionType type = lesion.getType();
                summary.put(type, summary.get(type) + 1);
            }
        }
        
        return summary;
    }
    
    /**
     * Genera un resumen de lesiones por región dental
     * @param odontogram Odontograma a analizar
     * @return Mapa de región a cantidad de lesiones
     */
    public Map<String, Integer> summarizeLesionsByRegion(Odontogram odontogram) {
        Map<String, Integer> summary = new HashMap<>();
        summary.put("Superior Derecha", 0);
        summary.put("Superior Izquierda", 0);
        summary.put("Inferior Izquierda", 0);
        summary.put("Inferior Derecha", 0);
        
        for (Tooth tooth : odontogram.getTeeth()) {
            String region = getRegionForTooth(tooth.getNumber());
            int currentCount = summary.get(region);
            summary.put(region, currentCount + tooth.getLesions().size());
        }
        
        return summary;
    }
    
    /**
     * Determina la región dental según el número de diente
     * @param toothNumber Número de diente
     * @return Región dental
     */
    private String getRegionForTooth(int toothNumber) {
        if (toothNumber >= 11 && toothNumber <= 18) return "Superior Derecha";
        if (toothNumber >= 21 && toothNumber <= 28) return "Superior Izquierda";
        if (toothNumber >= 31 && toothNumber <= 38) return "Inferior Izquierda";
        if (toothNumber >= 41 && toothNumber <= 48) return "Inferior Derecha";
        
        throw new DomainException("Número de diente fuera de rango: " + toothNumber);
    }
    
    /**
     * Calcula el riesgo de caries basado en el odontograma
     * @param odontogram Odontograma a analizar
     * @return Nivel de riesgo (BAJO, MEDIO, ALTO)
     */
    public RiskLevel calculateCariesRisk(Odontogram odontogram) {
        int cariesCount = 0;
        
        for (Tooth tooth : odontogram.getTeeth()) {
            for (Lesion lesion : tooth.getLesions()) {
                if (lesion.getType() == LesionType.CARIES || 
                    lesion.getType() == LesionType.CARIES_PROFUNDA) {
                    cariesCount++;
                }
            }
        }
        
        if (cariesCount >= 5) {
            return RiskLevel.ALTO;
        } else if (cariesCount >= 2) {
            return RiskLevel.MEDIO;
        } else {
            return RiskLevel.BAJO;
        }
    }
    
    /**
     * Niveles de riesgo para evaluación dental
     */
    public enum RiskLevel {
        BAJO, MEDIO, ALTO
    }
} 