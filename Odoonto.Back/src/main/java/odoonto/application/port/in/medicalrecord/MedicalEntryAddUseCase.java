package odoonto.application.port.in.medicalrecord;

import odoonto.application.dto.request.MedicalEntryCreateDTO;

/**
 * Caso de uso para añadir una entrada médica a un historial
 */
public interface MedicalEntryAddUseCase {
    void addEntry(String medicalRecordId, MedicalEntryCreateDTO entry);
} 