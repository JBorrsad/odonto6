package odoonto.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Objeto de valor que representa el identificador único de un paciente.
 * Es inmutable y puede generarse automáticamente o especificarse.
 */
public final class PatientId {
    private final String value;
    
    /**
     * Constructor privado para inicializar el valor
     * @param value Valor del identificador
     */
    private PatientId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador del paciente no puede estar vacío");
        }
        this.value = value;
    }
    
    /**
     * Crea un nuevo PatientId a partir de un valor existente
     * @param value Valor existente
     * @return Nuevo PatientId
     */
    public static PatientId of(String value) {
        return new PatientId(value);
    }
    
    /**
     * Genera un nuevo PatientId aleatorio usando UUID
     * @return Nuevo PatientId aleatorio
     */
    public static PatientId generate() {
        return new PatientId(UUID.randomUUID().toString());
    }
    
    /**
     * Obtiene el valor del identificador
     * @return Valor como String
     */
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientId that = (PatientId) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
} 