package odoonto.application.mapper;

import odoonto.domain.model.aggregates.Odontogram;

import odoonto.application.dto.response.OdontogramDTO;

import odoonto.application.dto.request.LesionCreateDTO;
import odoonto.domain.model.valueobjects.ToothFace;
import odoonto.domain.model.valueobjects.LesionType;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre Odontogram (dominio) y OdontogramDTO (aplicación)
 */
@Component
public class OdontogramMapper {
    
    /**
     * Convierte un objeto de dominio Odontogram a su representación DTO
     * @param odontogram Odontograma de dominio
     * @return DTO del odontograma
     */
    public OdontogramDTO toDTO(Odontogram odontogram) {
        if (odontogram == null) {
            return null;
        }
        
        Map<String, OdontogramDTO.ToothRecordDTO> teethDTOs = new HashMap<>();
        
        // Convertir cada registro de diente
        odontogram.getTeeth().forEach((toothId, toothRecord) -> {
            // Convertir mapa de lesiones (LesionType) a mapa de Strings
            Map<String, String> facesDTO = toothRecord.getFaces().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().toString()
                    ));
            
            teethDTOs.put(toothId, new OdontogramDTO.ToothRecordDTO(facesDTO));
        });
        
        // Crear el DTO
        return new OdontogramDTO(
                odontogram.getIdValue(),
                odontogram.extractPatientId() != null ? odontogram.extractPatientId().getValue() : null,
                teethDTOs
        );
    }
    
    /**
     * Convierte de DTO a objeto de dominio (para actualizaciones parciales)
     * @param dto DTO con datos para actualizar
     * @param existingOdontogram Odontograma existente que se actualizará
     * @return Odontograma actualizado
     */
    public Odontogram updateFromDTO(OdontogramDTO dto, Odontogram existingOdontogram) {
        if (dto == null || existingOdontogram == null) {
            return existingOdontogram;
        }
        
        // Solo actualizamos los dientes especificados en el DTO
        if (dto.getTeeth() != null) {
            dto.getTeeth().forEach((toothId, toothRecordDTO) -> {
                // Obtener o crear el registro del diente
                Odontogram.ToothRecord toothRecord = existingOdontogram.getTeeth()
                        .computeIfAbsent(toothId, k -> new Odontogram.ToothRecord());
                
                // Actualizar las caras/lesiones
                if (toothRecordDTO.getFaces() != null) {
                    // Limpiar las caras existentes y agregar las nuevas
                    toothRecord.getFaces().clear();
                    
                    toothRecordDTO.getFaces().forEach((face, lesionTypeStr) -> {
                        try {
                            LesionType lesionType = LesionType.valueOf(lesionTypeStr);
                            toothRecord.getFaces().put(face, lesionType);
                        } catch (IllegalArgumentException e) {
                            // Ignorar valores inválidos de tipo de lesión
                        }
                    });
                }
            });
        }
        
        return existingOdontogram;
    }
    
    /**
     * Convierte un DTO completo a un nuevo objeto de dominio
     * Solo para situaciones especiales donde es necesario crear uno nuevo
     * @param dto DTO fuente
     * @return Nuevo objeto de dominio
     */
    public Odontogram toDomain(OdontogramDTO dto) {
        // Normalmente, este método no se implementaría ya que los odontogramas
        // se crean a partir de un patientId y no de un DTO
        // Esta implementación es solo para completitud
        throw new UnsupportedOperationException("La creación de odontogramas debe hacerse a través de PatientId");
    }
    
    /**
     * Aplica un comando de lesión a un odontograma
     */
    public void applyLesionCommand(Odontogram odontogram, LesionCreateDTO lesionDTO) {
        if (odontogram == null || lesionDTO == null) {
            return;
        }
        
        ToothFace face = ToothFace.valueOf(lesionDTO.getFace());
        LesionType lesionType = LesionType.valueOf(lesionDTO.getLesionType());
        
        odontogram.addLesion(lesionDTO.getToothId(), face, lesionType);
    }
    
    /**
     * Remueve una lesión del odontograma
     */
    public void removeLesion(Odontogram odontogram, LesionCreateDTO lesionDTO) {
        if (odontogram == null || lesionDTO == null) {
            return;
        }
        
        ToothFace face = ToothFace.valueOf(lesionDTO.getFace());
        
        odontogram.removeLesion(lesionDTO.getToothId(), face);
    }
} 