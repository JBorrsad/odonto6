package odoonto.application.port.in.odontogram;

/**
 * Caso de uso para eliminar una lesión de un odontograma
 */
public interface LesionRemoveUseCase {
    /**
     * Elimina una lesión de un odontograma
     * @param odontogramId ID del odontograma
     * @param toothNumber Número del diente
     * @param lesionId ID de la lesión a eliminar
     */
    void removeLesion(String odontogramId, String toothNumber, String lesionId);
} 