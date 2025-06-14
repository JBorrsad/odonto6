package odoonto.domain.service;

import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.aggregates.Odontogram;

import odoonto.domain.model.valueobjects.LesionType;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Servicio de dominio para realizar diagnósticos dentales.
 * Encapsula lógica de negocio relacionada con la interpretación
 * del estado dental basado en el odontograma del paciente.
 */
public class DentalDiagnosisService {
    
    /**
     * Analiza un odontograma para generar un diagnóstico de la salud dental
     * @param odontogram Odontograma a analizar
     * @return Mapa con los resultados del diagnóstico
     */
    public Map<String, Object> generateDiagnosis(Odontogram odontogram) {
        if (odontogram == null) {
            throw new DomainException("No se puede generar un diagnóstico sin odontograma");
        }
        
        Map<String, Object> diagnosis = new HashMap<>();
        
        // Conteo de lesiones por tipo
        Map<LesionType, Integer> lesionCounts = countLesionsByType(odontogram);
        diagnosis.put("lesionCounts", lesionCounts);
        
        // Conteo de dientes afectados
        int affectedTeethCount = countAffectedTeeth(odontogram);
        int totalTeeth = odontogram.getTeeth().size();
        diagnosis.put("affectedTeethCount", affectedTeethCount);
        diagnosis.put("totalTeethCount", totalTeeth);
        
        // Porcentaje de salud dental
        double healthPercentage = calculateDentalHealthPercentage(affectedTeethCount, totalTeeth);
        diagnosis.put("healthPercentage", healthPercentage);
        
        // Nivel de riesgo
        String riskLevel = determineRiskLevel(healthPercentage, lesionCounts);
        diagnosis.put("riskLevel", riskLevel);
        
        // Áreas más afectadas
        List<String> mostAffectedAreas = identifyMostAffectedAreas(odontogram);
        diagnosis.put("mostAffectedAreas", mostAffectedAreas);
        
        // Tratamientos recomendados
        List<String> recommendedTreatments = suggestTreatments(lesionCounts, mostAffectedAreas);
        diagnosis.put("recommendedTreatments", recommendedTreatments);
        
        return diagnosis;
    }
    
    /**
     * Cuenta el número de lesiones por tipo
     * @param odontogram Odontograma a analizar
     * @return Mapa con conteo de lesiones por tipo
     */
    private Map<LesionType, Integer> countLesionsByType(Odontogram odontogram) {
        Map<LesionType, Integer> counts = new HashMap<>();
        
        for (Map.Entry<String, Odontogram.ToothRecord> entry : odontogram.getTeeth().entrySet()) {
            for (Map.Entry<String, LesionType> lesionEntry : entry.getValue().getFaces().entrySet()) {
                LesionType type = lesionEntry.getValue();
                counts.put(type, counts.getOrDefault(type, 0) + 1);
            }
        }
        
        return counts;
    }
    
    /**
     * Cuenta el número de dientes afectados por lesiones
     * @param odontogram Odontograma a analizar
     * @return Número de dientes afectados
     */
    private int countAffectedTeeth(Odontogram odontogram) {
        return odontogram.getTeeth().size();
    }
    
    /**
     * Calcula el porcentaje de salud dental
     * @param affectedTeethCount Número de dientes afectados
     * @param totalTeeth Número total de dientes
     * @return Porcentaje de salud dental (0-100)
     */
    private double calculateDentalHealthPercentage(int affectedTeethCount, int totalTeeth) {
        if (totalTeeth == 0) {
            return 100.0; // Por defecto, si no hay dientes se considera 100% de salud
        }
        
        double healthyTeethCount = totalTeeth - affectedTeethCount;
        return (healthyTeethCount / totalTeeth) * 100.0;
    }
    
    /**
     * Determina el nivel de riesgo basado en el porcentaje de salud y tipos de lesiones
     * @param healthPercentage Porcentaje de salud dental
     * @param lesionCounts Conteo de lesiones por tipo
     * @return Nivel de riesgo (BAJO, MEDIO, ALTO, MUY ALTO)
     */
    private String determineRiskLevel(double healthPercentage, Map<LesionType, Integer> lesionCounts) {
        // Contar lesiones severas
        int severeLesions = lesionCounts.getOrDefault(LesionType.CARIES, 0) +
                            lesionCounts.getOrDefault(LesionType.FRACTURA, 0);
        
        if (healthPercentage >= 90) {
            return "BAJO";
        } else if (healthPercentage >= 75) {
            return severeLesions > 0 ? "MEDIO" : "BAJO";
        } else if (healthPercentage >= 50) {
            return severeLesions > 2 ? "ALTO" : "MEDIO";
        } else {
            return "MUY ALTO";
        }
    }
    
    /**
     * Identifica las áreas más afectadas de la dentadura
     * @param odontogram Odontograma a analizar
     * @return Lista de áreas afectadas en orden de severidad
     */
    private List<String> identifyMostAffectedAreas(Odontogram odontogram) {
        // Conteo de lesiones por cuadrante
        Map<String, Integer> quadrantLesionCount = new HashMap<>();
        quadrantLesionCount.put("SUPERIOR_DERECHO", 0);
        quadrantLesionCount.put("SUPERIOR_IZQUIERDO", 0);
        quadrantLesionCount.put("INFERIOR_IZQUIERDO", 0);
        quadrantLesionCount.put("INFERIOR_DERECHO", 0);
        
        // Conteo de lesiones por zona
        Map<String, Integer> zoneLesionCount = new HashMap<>();
        zoneLesionCount.put("ANTERIOR", 0);
        zoneLesionCount.put("PREMOLARES", 0);
        zoneLesionCount.put("MOLARES", 0);
        
        for (Map.Entry<String, Odontogram.ToothRecord> entry : odontogram.getTeeth().entrySet()) {
            String toothId = entry.getKey();
            int toothNumber = Integer.parseInt(toothId);
            int lesionCount = entry.getValue().getFaces().size();
            
            // Determinar cuadrante
            String quadrant;
            if (toothNumber >= 11 && toothNumber <= 18) {
                quadrant = "SUPERIOR_DERECHO";
            } else if (toothNumber >= 21 && toothNumber <= 28) {
                quadrant = "SUPERIOR_IZQUIERDO";
            } else if (toothNumber >= 31 && toothNumber <= 38) {
                quadrant = "INFERIOR_IZQUIERDO";
            } else {
                quadrant = "INFERIOR_DERECHO";
            }
            
            // Determinar zona
            String zone;
            int toothPosition = toothNumber % 10;
            if (toothPosition >= 1 && toothPosition <= 3) {
                zone = "ANTERIOR";
            } else if (toothPosition >= 4 && toothPosition <= 5) {
                zone = "PREMOLARES";
            } else {
                zone = "MOLARES";
            }
            
            // Actualizar conteos
            quadrantLesionCount.put(quadrant, quadrantLesionCount.get(quadrant) + lesionCount);
            zoneLesionCount.put(zone, zoneLesionCount.get(zone) + lesionCount);
        }
        
        // Identificar áreas más afectadas
        List<String> affectedAreas = new ArrayList<>();
        
        // Añadir cuadrantes con más lesiones
        List<Map.Entry<String, Integer>> quadrantEntries = new ArrayList<>(quadrantLesionCount.entrySet());
        quadrantEntries.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        
        for (Map.Entry<String, Integer> entry : quadrantEntries) {
            if (entry.getValue() > 0) {
                affectedAreas.add("Cuadrante " + entry.getKey() + " (" + entry.getValue() + " lesiones)");
            }
        }
        
        // Añadir zonas con más lesiones
        List<Map.Entry<String, Integer>> zoneEntries = new ArrayList<>(zoneLesionCount.entrySet());
        zoneEntries.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        
        for (Map.Entry<String, Integer> entry : zoneEntries) {
            if (entry.getValue() > 0) {
                affectedAreas.add("Zona " + entry.getKey() + " (" + entry.getValue() + " lesiones)");
            }
        }
        
        return affectedAreas;
    }
    
    /**
     * Sugiere tratamientos basados en las lesiones encontradas
     * @param lesionCounts Conteo de lesiones por tipo
     * @param mostAffectedAreas Áreas más afectadas
     * @return Lista de tratamientos recomendados
     */
    private List<String> suggestTreatments(Map<LesionType, Integer> lesionCounts, List<String> mostAffectedAreas) {
        List<String> recommendations = new ArrayList<>();
        
        // Recomendaciones basadas en tipos de lesiones
        if (lesionCounts.getOrDefault(LesionType.CARIES, 0) > 0) {
            recommendations.add("Tratamiento para caries");
        }
        
        if (lesionCounts.getOrDefault(LesionType.DESGASTE, 0) > 0) {
            recommendations.add("Tratamiento para desgaste dental");
        }
        
        if (lesionCounts.getOrDefault(LesionType.FRACTURA, 0) > 0) {
            recommendations.add("Reconstrucción o corona para dientes fracturados");
        }
        
        // Recomendaciones generales basadas en la concentración de lesiones
        boolean hasManyLesions = lesionCounts.values().stream().mapToInt(Integer::intValue).sum() > 5;
        
        if (hasManyLesions) {
            recommendations.add("Plan integral de tratamiento dental");
            recommendations.add("Revisión de hábitos de higiene oral");
        }
        
        if (mostAffectedAreas.stream().anyMatch(area -> area.contains("ANTERIOR"))) {
            recommendations.add("Evaluación estética para zona anterior");
        }
        
        if (mostAffectedAreas.stream().anyMatch(area -> area.contains("MOLARES"))) {
            recommendations.add("Evaluación de hábitos de alimentación y técnica de cepillado");
        }
        
        return recommendations;
    }
    
    /**
     * Identifica los dientes que requieren atención inmediata en un odontograma
     * @param odontogram Odontograma a analizar (objeto de valor del paciente)
     * @return Lista de dientes que requieren atención inmediata
     */
    public List<String> identifyPriorityTeeth(Odontogram odontogram) {
        List<String> priorityTeeth = new ArrayList<>();
        
        for (Map.Entry<String, Odontogram.ToothRecord> entry : odontogram.getTeeth().entrySet()) {
            String toothId = entry.getKey();
            Odontogram.ToothRecord tooth = entry.getValue();
            
            for (LesionType lesionType : tooth.getFaces().values()) {
                if (requiresImmediateAttention(lesionType)) {
                    priorityTeeth.add(toothId);
                    break; // Ya encontramos una lesión que requiere atención inmediata en este diente
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
        return lesionType == LesionType.CARIES || 
               lesionType == LesionType.FRACTURA ||
               lesionType == LesionType.AUSENTE;
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
        
        for (Map.Entry<String, Odontogram.ToothRecord> entry : odontogram.getTeeth().entrySet()) {
            for (LesionType lesionType : entry.getValue().getFaces().values()) {
                summary.put(lesionType, summary.get(lesionType) + 1);
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
        
        for (Map.Entry<String, Odontogram.ToothRecord> entry : odontogram.getTeeth().entrySet()) {
            String toothId = entry.getKey();
            String region = getRegionForTooth(Integer.parseInt(toothId));
            int lesionCount = entry.getValue().getFaces().size();
            summary.put(region, summary.get(region) + lesionCount);
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
        
        for (Map.Entry<String, Odontogram.ToothRecord> entry : odontogram.getTeeth().entrySet()) {
            for (LesionType lesionType : entry.getValue().getFaces().values()) {
                if (lesionType == LesionType.CARIES) {
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