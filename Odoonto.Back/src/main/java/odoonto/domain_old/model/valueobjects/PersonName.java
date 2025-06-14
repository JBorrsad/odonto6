package odoonto.domain.model.valueobjects;

import java.util.Objects;

/**
 * Objeto de valor que representa el nombre completo de una persona.
 * Es inmutable y se valida en el constructor.
 */
public final class PersonName {
    private final String nombre;
    private final String primerApellido;
    private final String segundoApellido;
    
    /**
     * Constructor para crear un nombre completo válido.
     * 
     * @param nombre Nombre(s) de la persona
     * @param primerApellido Primer apellido
     * @param segundoApellido Segundo apellido (opcional)
     */
    public PersonName(String nombre, String primerApellido, String segundoApellido) {
        // Validar campos obligatorios
        validateNotBlank(nombre, "El nombre no puede estar vacío");
        validateNotBlank(primerApellido, "El primer apellido no puede estar vacío");
        validateNameFormat(nombre, "El nombre contiene caracteres inválidos");
        validateNameFormat(primerApellido, "El primer apellido contiene caracteres inválidos");
        
        // Validar formato del segundo apellido si está presente
        if (segundoApellido != null && !segundoApellido.trim().isEmpty()) {
            validateNameFormat(segundoApellido, "El segundo apellido contiene caracteres inválidos");
        }
        
        this.nombre = nombre.trim();
        this.primerApellido = primerApellido.trim();
        this.segundoApellido = segundoApellido != null ? segundoApellido.trim() : null;
    }
    
    /**
     * Constructor alternativo sin segundo apellido
     */
    public PersonName(String nombre, String primerApellido) {
        this(nombre, primerApellido, null);
    }
    
    /**
     * Valida que una cadena no sea nula o vacía
     * @param value Valor a validar
     * @param message Mensaje de error
     */
    private void validateNotBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Valida el formato del nombre (solo letras, espacios y caracteres especiales permitidos)
     * @param name Nombre a validar
     * @param message Mensaje de error
     */
    private void validateNameFormat(String name, String message) {
        // Patrón que permite letras, espacios, apóstrofes, guiones y acentos
        if (!name.matches("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\\s'\\-]+")) {
            throw new IllegalArgumentException(message);
        }
    }
    
    // Getters - No hay setters para mantener inmutabilidad
    
    public String getNombre() {
        return nombre;
    }
    
    public String getPrimerApellido() {
        return primerApellido;
    }
    
    public String getSegundoApellido() {
        return segundoApellido;
    }
    
    /**
     * Obtiene el nombre completo formateado
     * @return Nombre completo con formato
     */
    public String getFullName() {
        StringBuilder sb = new StringBuilder(nombre);
        sb.append(" ").append(primerApellido);
        
        if (segundoApellido != null && !segundoApellido.isEmpty()) {
            sb.append(" ").append(segundoApellido);
        }
        
        return sb.toString();
    }
    
    /**
     * Obtiene las iniciales de la persona
     * @return Iniciales (primera letra de cada componente del nombre)
     */
    public String getInitials() {
        StringBuilder initials = new StringBuilder();
        
        // Obtener inicial del nombre (solo la primera palabra)
        String[] nombreParts = nombre.split("\\s+");
        if (nombreParts.length > 0 && !nombreParts[0].isEmpty()) {
            initials.append(nombreParts[0].charAt(0));
        }
        
        // Obtener inicial del primer apellido
        if (!primerApellido.isEmpty()) {
            initials.append(primerApellido.charAt(0));
        }
        
        // Obtener inicial del segundo apellido si existe
        if (segundoApellido != null && !segundoApellido.isEmpty()) {
            initials.append(segundoApellido.charAt(0));
        }
        
        return initials.toString().toUpperCase();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        PersonName that = (PersonName) o;
        return Objects.equals(nombre, that.nombre) &&
               Objects.equals(primerApellido, that.primerApellido) &&
               Objects.equals(segundoApellido, that.segundoApellido);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(nombre, primerApellido, segundoApellido);
    }
    
    @Override
    public String toString() {
        return getFullName();
    }
} 