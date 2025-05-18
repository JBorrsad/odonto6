package odoonto.application.service.odontogram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.request.TreatmentCreateDTO;
import odoonto.application.port.in.odontogram.TreatmentAddUseCase;
import odoonto.application.port.out.ReactiveOdontogramRepository;
import odoonto.domain.exceptions.DomainException;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para añadir un tratamiento a un odontograma
 */
@Service
public class TreatmentAddService implements TreatmentAddUseCase {

    private final ReactiveOdontogramRepository odontogramRepository;

    @Autowired
    public TreatmentAddService(ReactiveOdontogramRepository odontogramRepository) {
        this.odontogramRepository = odontogramRepository;
    }

    @Override
    public void addTreatment(String odontogramId, String toothNumber, TreatmentCreateDTO treatment) {
        // Validaciones básicas
        if (odontogramId == null || odontogramId.trim().isEmpty()) {
            throw new DomainException("El ID del odontograma no puede ser nulo o vacío");
        }
        
        if (toothNumber == null || toothNumber.trim().isEmpty()) {
            throw new DomainException("El número de diente no puede ser nulo o vacío");
        }
        
        if (treatment == null) {
            throw new DomainException("Los datos del tratamiento no pueden ser nulos");
        }
        
        // Verificar que el odontograma existe antes de añadir el tratamiento
        odontogramRepository.findById(odontogramId)
            .switchIfEmpty(Mono.error(new DomainException("No existe un odontograma con el ID: " + odontogramId)))
            .flatMap(odontogram -> {
                // Añadir tratamiento usando el método del repositorio
                return odontogramRepository.addTreatment(odontogramId, toothNumber, treatment);
            })
            .block(); // Bloquear para esperar a que termine la operación
    }
} 