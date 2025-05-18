package odoonto.application.service.odontogram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.port.in.odontogram.TreatmentRemoveUseCase;
import odoonto.domain.exceptions.DomainException;
import odoonto.application.port.out.ReactiveOdontogramRepository;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para eliminar un tratamiento de un odontograma
 */
@Service
public class TreatmentRemoveService implements TreatmentRemoveUseCase {

    private final ReactiveOdontogramRepository odontogramRepository;

    @Autowired
    public TreatmentRemoveService(ReactiveOdontogramRepository odontogramRepository) {
        this.odontogramRepository = odontogramRepository;
    }

    @Override
    public Mono<Void> removeTreatment(String odontogramId, String toothNumber, String treatmentId) {
        // Validaciones básicas
        if (odontogramId == null || odontogramId.trim().isEmpty()) {
            return Mono.error(new DomainException("El ID del odontograma no puede ser nulo o vacío"));
        }
        
        if (toothNumber == null || toothNumber.trim().isEmpty()) {
            return Mono.error(new DomainException("El número de diente no puede ser nulo o vacío"));
        }
        
        if (treatmentId == null || treatmentId.trim().isEmpty()) {
            return Mono.error(new DomainException("El ID del tratamiento no puede ser nulo o vacío"));
        }
        
        // Verificar que el odontograma existe antes de eliminar el tratamiento
        return odontogramRepository.findById(odontogramId)
            .switchIfEmpty(Mono.error(new DomainException("No existe un odontograma con el ID: " + odontogramId)))
            .flatMap(odontogram -> 
                // Eliminar tratamiento usando el método del repositorio
                odontogramRepository.removeTreatment(odontogramId, toothNumber, treatmentId)
            );
    }
} 