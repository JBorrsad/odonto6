package odoonto.application.port.in.odontogram;

import odoonto.application.dto.response.OdontogramDTO;

/**
 * Puerto de entrada (caso de uso) para añadir lesiones a un odontograma
 */
public interface LesionAddUseCase {
    
    /**
     * Añade una lesión a un diente del odontograma
     * @param odontogramId ID del odontograma
     * @param toothNumber Número del diente
     * @param face Cara del diente
     * @param lesionType Tipo de lesión
     * @return DTO del odontograma actualizado
     */
    OdontogramDTO addLesion(String odontogramId, int toothNumber, String face, String lesionType);
} 