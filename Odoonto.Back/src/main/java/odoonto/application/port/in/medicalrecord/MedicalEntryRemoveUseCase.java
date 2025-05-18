package odoonto.application.port.in.medicalrecord;

/**
 * Caso de uso para eliminar una entrada médica de un historial
 */
public interface MedicalEntryRemoveUseCase {
    void removeEntry(String medicalRecordId, String entryId);
} 