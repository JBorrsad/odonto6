package odoonto.application.mapper;

import org.springframework.stereotype.Component;

import odoonto.application.dto.response.MedicalRecordDTO;
import odoonto.domain.model.aggregates.MedicalRecord;
import odoonto.domain.model.aggregates.MedicalRecord.MedicalNote;
import odoonto.domain.model.aggregates.MedicalRecord.Diagnosis;
import odoonto.domain.model.aggregates.MedicalRecord.Treatment;


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
        dto.setId(medicalRecord.getId().toString());
        
        // Mapear notas a entradas
        List<MedicalRecordDTO.MedicalEntryDTO> entries = mapNotesToEntryDTOs(medicalRecord.getNotes());
        
        // Añadir diagnósticos como entradas
        entries.addAll(mapDiagnosesToEntryDTOs(medicalRecord.getDiagnoses()));
        
        // Añadir tratamientos como entradas
        entries.addAll(mapTreatmentsToEntryDTOs(medicalRecord.getTreatments()));
        
        dto.setEntries(entries);
        
        // Establecer la fecha de última actualización
        dto.setLastUpdated(medicalRecord.getLastUpdatedAt().toLocalDate());
        
        // Establecer el ID del paciente
        dto.setPatientId(medicalRecord.getPatientId().toString());

        return dto;
    }

    /**
     * Convierte una lista de notas médicas a DTOs de entradas
     */
    private List<MedicalRecordDTO.MedicalEntryDTO> mapNotesToEntryDTOs(List<MedicalNote> notes) {
        if (notes == null) {
            return List.of();
        }

        return notes.stream()
                .map(this::mapNoteToEntryDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convierte una lista de diagnósticos a DTOs de entradas
     */
    private List<MedicalRecordDTO.MedicalEntryDTO> mapDiagnosesToEntryDTOs(List<Diagnosis> diagnoses) {
        if (diagnoses == null) {
            return List.of();
        }

        return diagnoses.stream()
                .map(this::mapDiagnosisToEntryDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convierte una lista de tratamientos a DTOs de entradas
     */
    private List<MedicalRecordDTO.MedicalEntryDTO> mapTreatmentsToEntryDTOs(List<Treatment> treatments) {
        if (treatments == null) {
            return List.of();
        }

        return treatments.stream()
                .map(this::mapTreatmentToEntryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una nota médica a un DTO de entrada
     */
    private MedicalRecordDTO.MedicalEntryDTO mapNoteToEntryDTO(MedicalNote note) {
        MedicalRecordDTO.MedicalEntryDTO entryDTO = new MedicalRecordDTO.MedicalEntryDTO();
        entryDTO.setId(note.getId().toString());
        entryDTO.setDescription(note.getContent());
        entryDTO.setDate(note.getCreatedAt().toLocalDate());
        entryDTO.setType("NOTA");
        entryDTO.setDoctorId(note.getDoctorId().toString());
        
        return entryDTO;
    }
    
    /**
     * Convierte un diagnóstico a un DTO de entrada
     */
    private MedicalRecordDTO.MedicalEntryDTO mapDiagnosisToEntryDTO(Diagnosis diagnosis) {
        MedicalRecordDTO.MedicalEntryDTO entryDTO = new MedicalRecordDTO.MedicalEntryDTO();
        entryDTO.setId(diagnosis.getId().toString());
        entryDTO.setDescription(diagnosis.getDescription());
        entryDTO.setDate(diagnosis.getCreatedAt().toLocalDate());
        entryDTO.setType("DIAGNÓSTICO");
        entryDTO.setDoctorId(diagnosis.getDoctorId().toString());
        
        return entryDTO;
    }
    
    /**
     * Convierte un tratamiento a un DTO de entrada
     */
    private MedicalRecordDTO.MedicalEntryDTO mapTreatmentToEntryDTO(Treatment treatment) {
        MedicalRecordDTO.MedicalEntryDTO entryDTO = new MedicalRecordDTO.MedicalEntryDTO();
        entryDTO.setId(treatment.getId().toString());
        entryDTO.setDescription(treatment.getDescription());
        entryDTO.setDate(treatment.getPrescriptionDate().toLocalDate());
        entryDTO.setType("TRATAMIENTO");
        entryDTO.setDoctorId(treatment.getDoctorId().toString());
        entryDTO.setNotes("Completado: " + (treatment.isCompleted() ? "Sí" : "No"));
        
        return entryDTO;
    }
} 