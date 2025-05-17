package odoonto.domain.service;

import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.entities.Tooth;
import odoonto.domain.model.entities.ToothNumber;
import odoonto.domain.model.valueobjects.LesionType;
import odoonto.domain.model.valueobjects.ToothFace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio de dominio para operaciones complejas sobre odontogramas.
 * Contiene lógica de negocio que no pertenece naturalmente a Odontogram o Tooth.
 */
public class OdontogramService {

    /**
     * Obtiene un resumen de las lesiones por tipo en todo el odontograma
     * @param odontogram Odontograma a analizar
     * @return Mapa con conteo de lesiones por tipo
     */
    public Map<LesionType, Integer> getLesionStatistics(Odontogram odontogram) {
        Map<LesionType, Integer> statistics = new HashMap<>();
        
        // Recorrer todos los dientes
        odontogram.getTeeth().forEach((toothId, toothRecord) -> {
            // Recorrer todas las caras con lesiones
            toothRecord.getFaces().forEach((faceCode, lesionType) -> {
                // Incrementar el contador para este tipo de lesión
                statistics.put(lesionType, statistics.getOrDefault(lesionType, 0) + 1);
            });
        });
        
        return statistics;
    }
    
    /**
     * Obtiene el número de lesiones totales en el odontograma
     * @param odontogram Odontograma a analizar
     * @return Número total de lesiones
     */
    public int countTotalLesions(Odontogram odontogram) {
        int count = 0;
        
        for (Map.Entry<String, Odontogram.ToothRecord> entry : odontogram.getTeeth().entrySet()) {
            count += entry.getValue().getFaces().size();
        }
        
        return count;
    }
    
    /**
     * Identifica los dientes que requieren atención prioritaria
     * @param odontogram Odontograma a analizar
     * @return Lista de IDs de dientes con lesiones graves (caries, pulpitis, etc.)
     */
    public List<String> getTeethRequiringUrgentAttention(Odontogram odontogram) {
        List<String> urgentTeeth = new ArrayList<>();
        
        // Define qué tipos de lesiones son consideradas urgentes
        List<LesionType> urgentLesionTypes = List.of(
            LesionType.CARIES, 
            LesionType.PULPITIS,
            LesionType.FRACTURA
        );
        
        // Recorrer todos los dientes
        odontogram.getTeeth().forEach((toothId, toothRecord) -> {
            // Verificar si alguna cara tiene una lesión urgente
            boolean hasUrgentLesion = toothRecord.getFaces().values().stream()
                .anyMatch(urgentLesionTypes::contains);
                
            if (hasUrgentLesion) {
                urgentTeeth.add(toothId);
            }
        });
        
        return urgentTeeth;
    }
    
    /**
     * Calcula el índice CPO-D (Cariados, Perdidos y Obturados)
     * Indicador epidemiológico para la salud dental
     * @param odontogram Odontograma a analizar
     * @return Valor del índice CPO-D
     */
    public int calculateCPODIndex(Odontogram odontogram) {
        int cariesCount = 0;
        int missingCount = 0;
        int filledCount = 0;
        
        // Recorrer todos los dientes
        for (Map.Entry<String, Odontogram.ToothRecord> entry : odontogram.getTeeth().entrySet()) {
            Map<String, LesionType> faces = entry.getValue().getFaces();
            
            // Si no hay faces registradas para el diente, pasar al siguiente
            if (faces.isEmpty()) {
                continue;
            }
            
            // Verificar si el diente está ausente
            if (faces.containsValue(LesionType.AUSENTE)) {
                missingCount++;
                continue;
            }
            
            // Verificar si el diente tiene caries
            if (faces.containsValue(LesionType.CARIES)) {
                cariesCount++;
                continue;
            }
            
            // Verificar si el diente tiene obturación
            if (faces.containsValue(LesionType.OBTURACION)) {
                filledCount++;
            }
        }
        
        // Retornar la suma de los tres componentes
        return cariesCount + missingCount + filledCount;
    }
    
    /**
     * Verifica si hay inconsistencias en el odontograma
     * (Por ejemplo, una cara marcada como ausente pero otras con lesiones)
     * @param odontogram Odontograma a validar
     * @return Lista de mensajes de inconsistencias o lista vacía si todo está bien
     */
    public List<String> validateOdontogramConsistency(Odontogram odontogram) {
        List<String> inconsistencies = new ArrayList<>();
        
        odontogram.getTeeth().forEach((toothId, toothRecord) -> {
            Map<String, LesionType> faces = toothRecord.getFaces();
            
            // Verificar si el diente está marcado como ausente pero tiene otras lesiones
            if (faces.containsValue(LesionType.AUSENTE) && faces.size() > 1) {
                inconsistencies.add("El diente " + toothId + " está marcado como ausente pero tiene otras lesiones");
            }
            
            // Verificar si hay lesiones incompatibles (por ejemplo, caries y obturación en la misma cara)
            faces.forEach((faceCode, lesionType) -> {
                for (Map.Entry<String, LesionType> otherFace : faces.entrySet()) {
                    if (otherFace.getKey().equals(faceCode)) {
                        continue; // Misma cara, ignorar
                    }
                    
                    if (faceCode.equals("C") && !otherFace.getKey().equals("C")) {
                        inconsistencies.add("El diente " + toothId + " está marcado como completo pero tiene lesiones en caras específicas");
                    }
                }
            });
        });
        
        return inconsistencies;
    }
    
    /**
     * Transfiere el odontograma a un nuevo modelo de Tooth para usar con las nuevas entidades
     * @param odontogram Odontograma en formato antiguo
     * @return Mapa de dientes en formato nuevo
     */
    public Map<String, Tooth> convertToToothModel(Odontogram odontogram) {
        Map<String, Tooth> teethMap = new HashMap<>();
        
        odontogram.getTeeth().forEach((toothId, toothRecord) -> {
            Tooth tooth = new Tooth(toothId);
            
            // Transferir lesiones
            toothRecord.getFaces().forEach((faceCode, lesionType) -> {
                try {
                    // Si es el diente completo, manejar diferente
                    if (faceCode.equals("C")) {
                        // Marcar todas las caras con esta lesión
                        for (ToothFace face : getAllValidFacesForTooth(toothId)) {
                            tooth.addLesion(face, lesionType);
                        }
                    } else {
                        // Cara específica
                        ToothFace face = ToothFace.fromCodigo(faceCode);
                        tooth.addLesion(face, lesionType);
                    }
                } catch (Exception e) {
                    // Ignorar caras inválidas
                }
            });
            
            teethMap.put(toothId, tooth);
        });
        
        return teethMap;
    }
    
    /**
     * Obtiene todas las caras válidas para un diente específico
     * @param toothId ID del diente
     * @return Lista de caras válidas
     */
    private List<ToothFace> getAllValidFacesForTooth(String toothId) {
        try {
            ToothNumber toothNumber = new ToothNumber(toothId);
            boolean isAnterior = toothNumber.isAnterior();
            
            return List.of(ToothFace.values()).stream()
                .filter(face -> face.isApplicable(isAnterior))
                .filter(face -> face != ToothFace.COMPLETO) // Excluir COMPLETO
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DomainException("Error al obtener caras para el diente: " + toothId);
        }
    }
} 