package odoonto.application.mapper;

import org.springframework.stereotype.Component;

import odoonto.application.dto.response.MedicalRecordDTO;
import odoonto.domain.model.aggregates.MedicalRecord;
import odoonto.domain.model.entities.MedicalEntry;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre MedicalRecord y MedicalRecordDTO
 */
@Component
public class MedicalRecordMapper {

    /**
     * Convierte una entidad MedicalRecord a su DTO
     * @param medicalRecord Entidad de dominio
     * @return DTO con los datos del historial médico
     */
    public MedicalRecordDTO toDTO(MedicalRecord medicalRecord) {
        if (medicalRecord == null) {
            return null;
        }

        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(medicalRecord.getId());
        dto.setEntries(mapEntriesToDTO(medicalRecord.getEntries()));
        dto.setCreatedAt(medicalRecord.getCreatedAt().toString());
        dto.setUpdatedAt(medicalRecord.getUpdatedAt().toString());

        return dto;
    }

    /**
     * Convierte una lista de entradas médicas a sus DTOs
     * @param entries Lista de entradas médicas
     * @return Lista de DTOs de entradas médicas
     */
    private List<MedicalRecordDTO.MedicalEntryDTO> mapEntriesToDTO(List<MedicalEntry> entries) {
        if (entries == null) {
            return List.of();
        }

        return entries.stream()
                .map(this::mapEntryToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entrada médica a su DTO
     * @param entry Entrada médica
     * @return DTO de la entrada médica
     */
    private MedicalRecordDTO.MedicalEntryDTO mapEntryToDTO(MedicalEntry entry) {
        MedicalRecordDTO.MedicalEntryDTO entryDTO = new MedicalRecordDTO.MedicalEntryDTO();
        entryDTO.setId(entry.getId());
        entryDTO.setDescription(entry.getDescription());
        entryDTO.setCreatedAt(entry.getCreatedAt().toString());
        
        return entryDTO;
    }
} 