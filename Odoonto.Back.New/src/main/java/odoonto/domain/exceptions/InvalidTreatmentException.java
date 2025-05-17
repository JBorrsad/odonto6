package odoonto.domain.exceptions;

/**
 * Excepción que se lanza cuando un tratamiento es inválido.
 */
public class InvalidTreatmentException extends DomainException {
    
    /**
     * Constructor con mensaje de error
     * @param message Mensaje descriptivo del error
     */
    public InvalidTreatmentException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje y causa
     * @param message Mensaje descriptivo del error
     * @param cause Causa original de la excepción
     */
    public InvalidTreatmentException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor específico para tratamiento no compatible con la lesión
     * @param treatmentType Tipo de tratamiento
     * @param lesionType Tipo de lesión
     * @return InvalidTreatmentException con mensaje específico
     */
    public static InvalidTreatmentException forIncompatibleLesion(String treatmentType, String lesionType) {
        return new InvalidTreatmentException(
                "El tratamiento '" + treatmentType + "' no es compatible con la lesión '" + 
                lesionType + "'.");
    }
    
    /**
     * Constructor específico para tratamiento inválido para un diente específico
     * @param treatmentType Tipo de tratamiento
     * @param toothNumber Número de diente
     * @return InvalidTreatmentException con mensaje específico
     */
    public static InvalidTreatmentException forInvalidToothType(String treatmentType, int toothNumber) {
        return new InvalidTreatmentException(
                "El tratamiento '" + treatmentType + "' no es aplicable para el diente " + 
                toothNumber + ".");
    }
    
    /**
     * Constructor específico para secuencia de tratamientos inválida
     * @param currentTreatment Tratamiento actual
     * @param previousRequired Tratamiento previo requerido
     * @return InvalidTreatmentException con mensaje específico
     */
    public static InvalidTreatmentException forInvalidSequence(String currentTreatment, String previousRequired) {
        return new InvalidTreatmentException(
                "El tratamiento '" + currentTreatment + "' requiere que primero se realice '" + 
                previousRequired + "'.");
    }
} 