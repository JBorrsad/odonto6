package odoonto.domain.model.valueobjects;

import odoonto.domain.exceptions.DomainException;

/**
 * Representa el número de un diente según la nomenclatura universal.
 * Objeto de valor que encapsula el identificador numérico del diente.
 */
public class ToothNumber {
    private final String value;
    
    /**
     * Constructor a partir de un valor numérico
     * @param number El número del diente (11-48 para permanentes, 51-85 para temporales)
     */
    public ToothNumber(int number) {
        this(String.valueOf(number));
    }
    
    /**
     * Constructor a partir de una cadena
     * @param number El número del diente como cadena
     */
    public ToothNumber(String number) {
        validate(number);
        this.value = number;
    }
    
    /**
     * Valida que el número de diente sea válido
     * @param toothNumber Número a validar
     * @throws DomainException Si el número no es válido
     */
    private void validate(String toothNumber) {
        if (toothNumber == null || toothNumber.trim().isEmpty()) {
            throw new DomainException("El número del diente no puede estar vacío");
        }
        
        try {
            int number = Integer.parseInt(toothNumber);
            
            // Validar dentición permanente (11-48)
            boolean validPermanent = number >= 11 && number <= 48;
            
            // Validar dentición temporal (51-85)
            boolean validTemporary = number >= 51 && number <= 85;
            
            if (!validPermanent && !validTemporary) {
                throw new DomainException("Número de diente fuera de rango: " + toothNumber);
            }
        } catch (NumberFormatException e) {
            throw new DomainException("Número de diente debe ser un valor numérico: " + toothNumber);
        }
    }
    
    /**
     * Determina si el diente pertenece a la dentición permanente
     * @return true si es diente permanente
     */
    public boolean isPermanent() {
        int number = Integer.parseInt(value);
        return number >= 11 && number <= 48;
    }
    
    /**
     * Determina si el diente pertenece a la dentición temporal
     * @return true si es diente temporal
     */
    public boolean isTemporary() {
        int number = Integer.parseInt(value);
        return number >= 51 && number <= 85;
    }
    
    /**
     * Determina si el diente es anterior (incisivos y caninos)
     * @return true si es diente anterior (posiciones 1, 2, 3 en cada cuadrante)
     */
    public boolean isAnterior() {
        int position = getPosition();
        // Posiciones 1, 2 y 3 corresponden a incisivos y caninos
        return position >= 1 && position <= 3;
    }
    
    /**
     * Determina si el diente es posterior (premolares y molares)
     * @return true si es diente posterior (posiciones 4-8 en cada cuadrante)
     */
    public boolean isPosterior() {
        int position = getPosition();
        // Posiciones 4-8 corresponden a premolares y molares
        return position >= 4 && position <= 8;
    }
    
    /**
     * Obtiene el cuadrante al que pertenece el diente
     * @return Número de cuadrante (1-4 para permanentes, 5-8 para temporales)
     */
    public int getQuadrant() {
        int number = Integer.parseInt(value);
        return number / 10;
    }
    
    /**
     * Obtiene la posición dentro del cuadrante
     * @return Posición (1-8)
     */
    public int getPosition() {
        int number = Integer.parseInt(value);
        return number % 10;
    }
    
    /**
     * Obtiene el valor como cadena
     * @return Valor del número del diente
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Devuelve el valor numérico del diente
     * @return Valor como entero
     */
    public int toInt() {
        return Integer.parseInt(value);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ToothNumber)) return false;
        ToothNumber that = (ToothNumber) o;
        return value.equals(that.value);
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public String toString() {
        return value;
    }
} 