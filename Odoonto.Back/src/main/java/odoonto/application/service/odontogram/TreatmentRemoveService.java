package odoonto.application.service.odontogram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.port.in.odontogram.TreatmentRemoveUseCase;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.repository.OdontogramRepository;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para eliminar un tratamiento de un odontograma
 */
@Service
public class TreatmentRemoveService implements TreatmentRemoveUseCase {

    private final OdontogramRepository odontogramRepository;

    @Autowired
    public TreatmentRemoveService(OdontogramRepository odontogramRepository) {
        this.odontogramRepository = odontogramRepository;
    }

    @Override
    public void removeTreatment(String odontogramId, String toothNumber, String treatmentId) {
        // Validaciones básicas
        if (odontogramId == null || odontogramId.trim().isEmpty()) {
            throw new DomainException("El ID del odontograma no puede ser nulo o vacío");
        }
        
        if (toothNumber == null || toothNumber.trim().isEmpty()) {
            throw new DomainException("El número de diente no puede ser nulo o vacío");
        }
        
        if (treatmentId == null || treatmentId.trim().isEmpty()) {
            throw new DomainException("El ID del tratamiento no puede ser nulo o vacío");
        }
        
        // Verificar que el odontograma existe antes de eliminar el tratamiento
        odontogramRepository.findById(odontogramId)
            .switchIfEmpty(Mono.error(new DomainException("No existe un odontograma con el ID: " + odontogramId)))
            .flatMap(odontogram -> {
                // Eliminar tratamiento usando el método del repositorio
                return odontogramRepository.removeTreatment(odontogramId, toothNumber, treatmentId);
            })
            .block(); // Bloquear para esperar a que termine la operación
    }
} 