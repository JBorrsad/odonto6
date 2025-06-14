package odoonto.domain.model.valueobjects;

import odoonto.domain.exceptions.DomainException;

/**
 * Objeto de valor que representa un número de teléfono válido
 */
public class PhoneNumber {
    private final String value;
    
    /**
     * Constructor que valida el formato del número de teléfono
     * @param value El número de teléfono
     * @throws DomainException si el número no es válido
     */
    public PhoneNumber(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("El número de teléfono no puede estar vacío");
        }
        
        // Eliminar espacios y caracteres no numéricos excepto + al inicio
        String cleaned = value.replaceAll("[\\s\\-()]", "");
        
        // Validar que solo contiene dígitos (permitiendo un + al inicio)
        if (!cleaned.matches("^\\+?[0-9]{8,15}$")) {
            throw new DomainException("El formato del número de teléfono no es válido: " + value);
        }
        
        this.value = cleaned;
    }
    
    /**
     * Devuelve el valor del número de teléfono
     * @return El número de teléfono
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Devuelve el número de teléfono formateado para visualización
     * @return El número de teléfono formateado
     */
    public String getFormattedValue() {
        
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        PhoneNumber that = (PhoneNumber) o;
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
