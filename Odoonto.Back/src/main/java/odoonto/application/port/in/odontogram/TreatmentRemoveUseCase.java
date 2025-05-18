package odoonto.application.port.in.odontogram;

import reactor.core.publisher.Mono;

/**
 * Caso de uso para eliminar un tratamiento de un odontograma
 */
public interface TreatmentRemoveUseCase {
    /**
     * Elimina un tratamiento de un diente en un odontograma
     * @param odontogramId ID del odontograma
     * @param toothNumber Número del diente
     * @param treatmentId ID del tratamiento a eliminar
     * @return Mono que completa cuando la operación termina
     */
    Mono<Void> removeTreatment(String odontogramId, String toothNumber, String treatmentId);
} 