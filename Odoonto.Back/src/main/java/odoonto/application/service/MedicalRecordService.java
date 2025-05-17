package odoonto.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.response.MedicalRecordDTO;
import odoonto.application.exceptions.MedicalRecordNotFoundException;
import odoonto.application.mapper.MedicalRecordMapper;
import odoonto.domain.model.aggregates.MedicalRecord;
import odoonto.domain.model.entities.MedicalEntry;
import odoonto.domain.model.valueobjects.MedicalRecordId;
import odoonto.domain.repository.MedicalRecordRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    @Autowired
    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository, 
                                MedicalRecordMapper medicalRecordMapper) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicalRecordMapper = medicalRecordMapper;
    }

    /**
     * Obtiene un historial médico por su ID
     * @param id ID del historial médico
     * @return DTO del historial médico
     * @throws MedicalRecordNotFoundException si no se encuentra el historial
     */
    public MedicalRecordDTO getMedicalRecordById(String id) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new MedicalRecordNotFoundException("Historial médico no encontrado con ID: " + id));
        
        return medicalRecordMapper.toDTO(medicalRecord);
    }

    /**
     * Añade una entrada al historial médico
     * @param id ID del historial médico
     * @param entryDescription Descripción de la entrada
     * @return Historial médico actualizado
     * @throws MedicalRecordNotFoundException si no se encuentra el historial
     */
    public MedicalRecordDTO addMedicalEntry(String id, String entryDescription) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new MedicalRecordNotFoundException("Historial médico no encontrado con ID: " + id));
        
        // Crear nueva entrada médica
        MedicalEntry entry = new MedicalEntry(
                UUID.randomUUID().toString(),
                entryDescription,
                LocalDateTime.now()
        );
        
        // Añadir entrada al historial
        medicalRecord.addEntry(entry);
        
        // Guardar historial actualizado
        medicalRecordRepository.save(medicalRecord);
        
        return medicalRecordMapper.toDTO(medicalRecord);
    }

    /**
     * Elimina una entrada del historial médico
     * @param id ID del historial médico
     * @param entryId ID de la entrada a eliminar
     * @return Historial médico actualizado
     * @throws MedicalRecordNotFoundException si no se encuentra el historial
     */
    public MedicalRecordDTO removeMedicalEntry(String id, String entryId) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new MedicalRecordNotFoundException("Historial médico no encontrado con ID: " + id));
        
        // Eliminar entrada del historial
        medicalRecord.removeEntry(entryId);
        
        // Guardar historial actualizado
        medicalRecordRepository.save(medicalRecord);
        
        return medicalRecordMapper.toDTO(medicalRecord);
    }

    /**
     * Crea un nuevo historial médico para un paciente
     * @param patientId ID del paciente
     * @return DTO del historial médico creado
     */
    public MedicalRecordDTO createMedicalRecord(String patientId) {
        // Crear ID derivado del ID del paciente
        MedicalRecordId medicalRecordId = MedicalRecordId.fromPatientId(patientId);
        
        // Crear nuevo historial médico
        MedicalRecord medicalRecord = new MedicalRecord(medicalRecordId.getValue());
        
        // Guardar historial
        medicalRecordRepository.save(medicalRecord);
        
        return medicalRecordMapper.toDTO(medicalRecord);
    }
} 