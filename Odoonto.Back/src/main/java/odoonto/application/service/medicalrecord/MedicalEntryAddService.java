package odoonto.application.service.medicalrecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.request.MedicalEntryCreateDTO;
import odoonto.application.port.in.medicalrecord.MedicalEntryAddUseCase;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.entities.MedicalEntry;
import odoonto.domain.model.entities.MedicalRecord;
import odoonto.domain.repository.MedicalRecordRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del caso de uso para añadir entradas médicas a un historial
 */
@Service
public class MedicalEntryAddService implements MedicalEntryAddUseCase {

    private final MedicalRecordRepository medicalRecordRepository;

    @Autowired
    public MedicalEntryAddService(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Override
    public void addEntry(String medicalRecordId, MedicalEntryCreateDTO entryDTO) {
        // Validaciones básicas
        if (medicalRecordId == null || medicalRecordId.trim().isEmpty()) {
            throw new DomainException("El ID del historial médico no puede ser nulo o vacío");
        }
        
        if (entryDTO == null) {
            throw new DomainException("Los datos de la entrada médica no pueden ser nulos");
        }
        
        if (entryDTO.getDescription() == null || entryDTO.getDescription().trim().isEmpty()) {
            throw new DomainException("La descripción de la entrada médica no puede ser nula o vacía");
        }
        
        // Buscar el historial médico
        Optional<MedicalRecord> medicalRecordOpt = medicalRecordRepository.findById(medicalRecordId);
        
        if (!medicalRecordOpt.isPresent()) {
            throw new DomainException("No existe un historial médico con el ID: " + medicalRecordId);
        }
        
        MedicalRecord medicalRecord = medicalRecordOpt.get();
        
        // Crear una nueva entrada médica
        MedicalEntry newEntry = new MedicalEntry(
            UUID.randomUUID().toString(),
            entryDTO.getType(),
            entryDTO.getDescription(),
            entryDTO.getDoctorId()
        );
        
        // Añadir la entrada al historial médico
        medicalRecord.addEntry(newEntry);
        
        // Guardar el historial médico actualizado
        medicalRecordRepository.save(medicalRecord);
    }
} 