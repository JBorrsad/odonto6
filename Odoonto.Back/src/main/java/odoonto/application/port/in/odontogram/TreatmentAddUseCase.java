package odoonto.application.port.in.odontogram;

import odoonto.application.dto.request.TreatmentCreateDTO;

/**
 * Caso de uso para añadir un tratamiento a un odontograma
 */
public interface TreatmentAddUseCase {
    /**
     * Añade un tratamiento a un diente en un odontograma
     * @param odontogramId ID del odontograma
     * @param toothNumber Número del diente
     * @param treatment DTO con los datos del tratamiento
     */
    void addTreatment(String odontogramId, String toothNumber, TreatmentCreateDTO treatment);
} 