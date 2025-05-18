package odoonto.application.port.in.odontogram;

import reactor.core.publisher.Mono;

/**
 * Caso de uso para eliminar una lesión de un odontograma
 */
public interface LesionRemoveUseCase {
    /**
     * Elimina una lesión de un odontograma
     * @param odontogramId ID del odontograma
     * @param toothNumber Número del diente
     * @param lesionId ID de la lesión a eliminar
     * @return Mono que completa cuando la operación termina
     */
    Mono<Void> removeLesion(String odontogramId, String toothNumber, String lesionId);
} 