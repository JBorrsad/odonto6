package odoonto.application.port.in.medicalrecord;

import reactor.core.publisher.Mono;

/**
 * Caso de uso para eliminar una entrada médica de un historial
 */
public interface MedicalEntryRemoveUseCase {
    Mono<Void> removeEntry(String medicalRecordId, String entryId);
} 