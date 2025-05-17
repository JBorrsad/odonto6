package odoonto.domain.model.valueobjects;

import odoonto.domain.exceptions.DomainException;
import java.util.regex.Pattern;

/**
 * Objeto de valor que representa una dirección de correo electrónico válida
 */
public class EmailAddress {
    // Expresión regular para validar email según RFC 5322
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    
    private final String value;
    
    /**
     * Constructor que valida el formato del email
     * @param value La dirección de email
     * @throws DomainException si el email no es válido
     */
    public EmailAddress(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("El email no puede estar vacío");
        }
        
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new DomainException("El formato del email no es válido: " + value);
        }
        
        this.value = value;
    }
    
    /**
     * Devuelve el valor del email
     * @return La dirección de email
     */
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        EmailAddress that = (EmailAddress) o;
        return value.equalsIgnoreCase(that.value);
    }
    
    @Override
    public int hashCode() {
        return value.toLowerCase().hashCode();
    }
    
    @Override
    public String toString() {
        return value;
    }
}
