package odoonto.domain.model.valueobjects;

import java.util.Objects;

/**
 * Objeto de valor que representa una dirección física.
 * Es inmutable y se valida en el constructor.
 */
public final class Address {
    private final String calle;
    private final String numero;
    private final String colonia;
    private final String ciudad;
    private final String estado;
    private final String codigoPostal;
    private final String referencia;
    
    /**
     * Constructor para crear una dirección válida.
     * Se asegura que los campos obligatorios estén presentes.
     * 
     * @param calle Nombre de la calle
     * @param numero Número exterior
     * @param colonia Colonia o fraccionamiento
     * @param ciudad Ciudad
     * @param estado Estado o provincia
     * @param codigoPostal Código postal
     * @param referencia Referencia adicional (opcional)
     */
    public Address(String calle, String numero, String colonia, 
                   String ciudad, String estado, String codigoPostal, 
                   String referencia) {
        // Validar campos obligatorios
        validateNotBlank(calle, "La calle no puede estar vacía");
        validateNotBlank(ciudad, "La ciudad no puede estar vacía");
        validateNotBlank(estado, "El estado no puede estar vacío");
        
        // Validar formato de código postal (opcional si está presente)
        if (codigoPostal != null && !codigoPostal.trim().isEmpty()) {
            validatePostalCode(codigoPostal);
        }
        
        this.calle = calle;
        this.numero = numero;
        this.colonia = colonia;
        this.ciudad = ciudad;
        this.estado = estado;
        this.codigoPostal = codigoPostal;
        this.referencia = referencia;
    }
    
    /**
     * Constructor alternativo sin referencia
     */
    public Address(String calle, String numero, String colonia, 
                   String ciudad, String estado, String codigoPostal) {
        this(calle, numero, colonia, ciudad, estado, codigoPostal, null);
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
     * Valida el formato del código postal
     * @param postalCode Código postal a validar
     */
    private void validatePostalCode(String postalCode) {
        // En México los códigos postales son de 5 dígitos
        if (!postalCode.matches("\\d{5}")) {
            throw new IllegalArgumentException("El código postal debe contener 5 dígitos");
        }
    }
    
    // Getters - No hay setters para mantener inmutabilidad
    
    public String getCalle() {
        return calle;
    }
    
    public String getNumero() {
        return numero;
    }
    
    public String getColonia() {
        return colonia;
    }
    
    public String getCiudad() {
        return ciudad;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public String getCodigoPostal() {
        return codigoPostal;
    }
    
    public String getReferencia() {
        return referencia;
    }
    
    /**
     * Obtiene la dirección formateada como string
     * @return Cadena con formato de dirección completa
     */
    public String getFormattedAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(calle);
        
        if (numero != null && !numero.isEmpty()) {
            sb.append(" #").append(numero);
        }
        
        if (colonia != null && !colonia.isEmpty()) {
            sb.append(", Col. ").append(colonia);
        }
        
        sb.append(", ").append(ciudad);
        sb.append(", ").append(estado);
        
        if (codigoPostal != null && !codigoPostal.isEmpty()) {
            sb.append(", C.P. ").append(codigoPostal);
        }
        
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Address address = (Address) o;
        return Objects.equals(calle, address.calle) &&
               Objects.equals(numero, address.numero) &&
               Objects.equals(colonia, address.colonia) &&
               Objects.equals(ciudad, address.ciudad) &&
               Objects.equals(estado, address.estado) &&
               Objects.equals(codigoPostal, address.codigoPostal);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(calle, numero, colonia, ciudad, estado, codigoPostal);
    }
    
    @Override
    public String toString() {
        return getFormattedAddress();
    }
} 