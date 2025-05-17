package odoonto.application.mapper;

import odoonto.domain.model.aggregates.Odontogram;
import odoonto.domain.model.aggregates.Odontogram.ToothRecord;
import odoonto.application.dto.response.OdontogramDTO;
import odoonto.application.dto.response.OdontogramDTO.ToothRecordDTO;
import odoonto.application.dto.request.LesionCreateDTO;
import odoonto.domain.model.valueobjects.ToothFace;
import odoonto.domain.model.valueobjects.LesionType;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre Odontogram y sus DTOs
 */
@Component
public class OdontogramMapper {
    
    /**
     * Convierte un Odontogram a OdontogramDTO
     */
    public OdontogramDTO toDTO(Odontogram odontogram) {
        if (odontogram == null) {
            return null;
        }
        
        Map<String, ToothRecordDTO> teethDTO = new HashMap<>();
        
        odontogram.getTeeth().forEach((toothId, toothRecord) -> {
            Map<String, String> facesDTO = toothRecord.getFaces().entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> entry.getKey(),
                    entry -> entry.getValue().toString()
                ));
            
            teethDTO.put(toothId, new ToothRecordDTO(facesDTO));
        });
        
        return new OdontogramDTO(teethDTO);
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