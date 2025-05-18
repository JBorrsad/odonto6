package odoonto.application.port.in.medicalrecord;

import odoonto.application.dto.request.MedicalEntryCreateDTO;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para añadir una entrada médica a un historial
 */
public interface MedicalEntryAddUseCase {
    Mono<Void> addEntry(String medicalRecordId, MedicalEntryCreateDTO entry);
} 