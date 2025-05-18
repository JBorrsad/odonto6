package odoonto.application.service.medicalrecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.port.in.medicalrecord.MedicalEntryRemoveUseCase;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.repository.MedicalRecordRepository;

/**
 * Implementación del caso de uso para eliminar entradas médicas de un historial
 */
@Service
public class MedicalEntryRemoveService implements MedicalEntryRemoveUseCase {

    private final MedicalRecordRepository medicalRecordRepository;

    @Autowired
    public MedicalEntryRemoveService(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Override
    public void removeEntry(String medicalRecordId, String entryId) {
        // Validaciones básicas
        if (medicalRecordId == null || medicalRecordId.trim().isEmpty()) {
            throw new DomainException("El ID del historial médico no puede ser nulo o vacío");
        }
        
        if (entryId == null || entryId.trim().isEmpty()) {
            throw new DomainException("El ID de la entrada médica no puede ser nulo o vacío");
        }
        
        // Verificar que el historial médico existe
        if (!medicalRecordRepository.existsById(medicalRecordId)) {
            throw new DomainException("No existe un historial médico con el ID: " + medicalRecordId);
        }
        
        // Eliminar la entrada médica usando el método del repositorio
        boolean success = medicalRecordRepository.deleteEntry(medicalRecordId, entryId);
        
        if (!success) {
            throw new DomainException("No se pudo eliminar la entrada médica con ID: " + entryId);
        }
    }
} 