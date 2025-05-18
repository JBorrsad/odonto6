package odoonto.application.mapper;

import org.springframework.stereotype.Component;

import odoonto.application.dto.response.MedicalRecordDTO;
import odoonto.domain.model.entities.MedicalRecord;
import odoonto.domain.model.entities.MedicalEntry;

import java.time.LocalDate;
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
        dto.setId(medicalRecord.getIdValue());
        dto.setEntries(mapEntriesToDTO(medicalRecord.getEntries()));
        
        // Establecer la fecha de última actualización como la fecha actual
        dto.setLastUpdated(LocalDate.now());
        
        // Si hay un PatientId asociado, establecerlo
        if (medicalRecord.extractPatientId() != null) {
            dto.setPatientId(medicalRecord.extractPatientId().getValue());
        }

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
        entryDTO.setDate(entry.getRecordedAt().toLocalDate());
        entryDTO.setType(entry.getType());
        
        if (entry.getDoctorId() != null) {
            entryDTO.setDoctorId(entry.getDoctorId());
        }
        
        if (entry.getNotes() != null) {
            entryDTO.setNotes(entry.getNotes());
        }
        
        return entryDTO;
    }
} 