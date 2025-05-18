package odoonto.domain.model.entities;

import odoonto.domain.exceptions.DomainException;

/**
 * Entidad que representa el número de un diente según la notación FDI/ISO
 * - Primer dígito: cuadrante (1-4)
 * - Segundo dígito: posición en el cuadrante (1-8)
 */
public class ToothNumber {
    private final String value;
    private final int quadrant;
    private final int position;
    
    /**
     * Constructor que valida y almacena el número de diente
     * @param value El número del diente en formato de dos dígitos
     * @throws DomainException Si el formato es inválido
     */
    public ToothNumber(String value) {
        if (value == null || value.length() != 2) {
            throw new DomainException("El número de diente debe tener exactamente 2 dígitos");
        }
        
        try {
            this.quadrant = Integer.parseInt(value.substring(0, 1));
            this.position = Integer.parseInt(value.substring(1, 2));
            
            if (quadrant < 1 || quadrant > 4) {
                throw new DomainException("El cuadrante debe estar entre 1 y 4");
            }
            
            if (position < 1 || position > 8) {
                throw new DomainException("La posición debe estar entre 1 y 8");
            }
            
            this.value = value;
        } catch (NumberFormatException e) {
            throw new DomainException("El número de diente debe contener solo dígitos");
        }
    }
    
    /**
     * Obtiene el valor del número de diente
     * @return Valor como string de dos dígitos
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Obtiene el cuadrante del diente (1-4)
     * @return Número de cuadrante
     */
    public int getQuadrant() {
        return quadrant;
    }
    
    /**
     * Obtiene la posición del diente en el cuadrante (1-8)
     * @return Posición
     */
    public int getPosition() {
        return position;
    }
    
    /**
     * Determina si el diente es anterior (incisivos y caninos)
     * @return true si es un diente anterior (posiciones 1-3)
     */
    public boolean isAnterior() {
        return position >= 1 && position <= 3;
    }
    
    /**
     * Determina si el diente es un premolar
     * @return true si es un premolar (posiciones 4-5)
     */
    public boolean isPremolar() {
        return position >= 4 && position <= 5;
    }
    
    /**
     * Determina si el diente es un molar
     * @return true si es un molar (posiciones 6-8)
     */
    public boolean isMolar() {
        return position >= 6 && position <= 8;
    }
    
    /**
     * Determina si el diente pertenece a la arcada superior
     * @return true si pertenece a la arcada superior (cuadrantes 1-2)
     */
    public boolean isUpper() {
        return quadrant == 1 || quadrant == 2;
    }
    
    /**
     * Determina si el diente pertenece a la arcada inferior
     * @return true si pertenece a la arcada inferior (cuadrantes 3-4)
     */
    public boolean isLower() {
        return quadrant == 3 || quadrant == 4;
    }
    
    /**
     * Determina si el diente está en el lado derecho
     * @return true si está en el lado derecho (cuadrantes 1,4)
     */
    public boolean isRight() {
        return quadrant == 1 || quadrant == 4;
    }
    
    /**
     * Determina si el diente está en el lado izquierdo
     * @return true si está en el lado izquierdo (cuadrantes 2,3)
     */
    public boolean isLeft() {
        return quadrant == 2 || quadrant == 3;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ToothNumber other = (ToothNumber) obj;
        return value.equals(other.value);
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
} 