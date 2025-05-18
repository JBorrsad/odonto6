package odoonto.application.service.medicalrecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.port.in.medicalrecord.MedicalEntryRemoveUseCase;
import odoonto.application.port.out.ReactiveMedicalRecordRepository;
import odoonto.domain.exceptions.DomainException;
import reactor.core.publisher.Mono;

/**
 * Implementación reactiva del caso de uso para eliminar entradas médicas de un historial
 */
@Service
public class MedicalEntryRemoveService implements MedicalEntryRemoveUseCase {

    private final ReactiveMedicalRecordRepository medicalRecordRepository;

    @Autowired
    public MedicalEntryRemoveService(ReactiveMedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    @Override
    public Mono<Void> removeEntry(String medicalRecordId, String entryId) {
        // Validaciones básicas
        if (medicalRecordId == null || medicalRecordId.trim().isEmpty()) {
            return Mono.error(new DomainException("El ID del historial médico no puede ser nulo o vacío"));
        }
        
        if (entryId == null || entryId.trim().isEmpty()) {
            return Mono.error(new DomainException("El ID de la entrada médica no puede ser nulo o vacío"));
        }
        
        // Verificar que el historial médico existe y eliminar la entrada médica
        return medicalRecordRepository.findById(medicalRecordId)
            .switchIfEmpty(Mono.error(new DomainException("No existe un historial médico con el ID: " + medicalRecordId)))
            .flatMap(medicalRecord -> medicalRecordRepository.deleteEntry(
                medicalRecordId, 
                entryId)
                .flatMap(success -> {
                    if (!success) {
                        return Mono.error(new DomainException("No se pudo eliminar la entrada médica con ID: " + entryId));
                    }
                    return Mono.empty();
                })
            )
            .then();
    }
} 