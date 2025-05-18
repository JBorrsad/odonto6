package odoonto.application.service.odontogram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.port.in.odontogram.LesionRemoveUseCase;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.repository.OdontogramRepository;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para eliminar una lesión de un odontograma
 */
@Service
public class LesionRemoveService implements LesionRemoveUseCase {

    private final OdontogramRepository odontogramRepository;

    @Autowired
    public LesionRemoveService(OdontogramRepository odontogramRepository) {
        this.odontogramRepository = odontogramRepository;
    }

    @Override
    public void removeLesion(String odontogramId, String toothNumber, String lesionId) {
        // Validaciones básicas
        if (odontogramId == null || odontogramId.trim().isEmpty()) {
            throw new DomainException("El ID del odontograma no puede ser nulo o vacío");
        }
        
        if (toothNumber == null || toothNumber.trim().isEmpty()) {
            throw new DomainException("El número de diente no puede ser nulo o vacío");
        }
        
        if (lesionId == null || lesionId.trim().isEmpty()) {
            throw new DomainException("El ID de la lesión no puede ser nulo o vacío");
        }
        
        // Verificar que el odontograma existe antes de eliminar la lesión
        odontogramRepository.findById(odontogramId)
            .switchIfEmpty(Mono.error(new DomainException("No existe un odontograma con el ID: " + odontogramId)))
            .flatMap(odontogram -> {
                // Eliminar lesión usando el método del repositorio
                return odontogramRepository.removeLesion(odontogramId, toothNumber, lesionId);
            })
            .block(); // Bloquear para esperar a que termine la operación
    }
} 