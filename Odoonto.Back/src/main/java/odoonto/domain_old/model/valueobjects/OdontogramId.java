package odoonto.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Objeto de valor que representa el identificador único de un odontograma.
 * Es inmutable y puede derivarse de un PatientId o especificarse directamente.
 */
public final class OdontogramId {
    private final String value;
    
    /**
     * Constructor privado para inicializar el valor
     * @param value Valor del identificador
     */
    private OdontogramId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador del odontograma no puede estar vacío");
        }
        this.value = value;
    }
    
    /**
     * Crea un nuevo OdontogramId a partir de un valor existente
     * @param value Valor existente
     * @return Nuevo OdontogramId
     */
    public static OdontogramId of(String value) {
        return new OdontogramId(value);
    }
    
    /**
     * Genera un nuevo OdontogramId aleatorio usando UUID
     * @return Nuevo OdontogramId aleatorio
     */
    public static OdontogramId generate() {
        return new OdontogramId(UUID.randomUUID().toString());
    }
    
    /**
     * Crea un nuevo OdontogramId derivado a partir de un PatientId
     * @param patientId Identificador del paciente asociado
     * @return Nuevo OdontogramId
     */
    public static OdontogramId fromPatientId(PatientId patientId) {
        if (patientId == null) {
            throw new IllegalArgumentException("El identificador del paciente no puede ser nulo");
        }
        return new OdontogramId("odontogram_" + patientId.getValue());
    }
    
    /**
     * Extrae el identificador del paciente si este OdontogramId fue creado a partir de un PatientId
     * @return PatientId extraído o null si no fue derivado de un paciente
     */
    public PatientId extractPatientId() {
        if (value.startsWith("odontogram_")) {
            String patientIdValue = value.substring("odontogram_".length());
            return PatientId.of(patientIdValue);
        }
        return null;
    }
    
    /**
     * Verifica si este OdontogramId fue derivado de un PatientId
     * @return true si fue derivado, false en caso contrario
     */
    public boolean isDerivedFromPatient() {
        return value.startsWith("odontogram_");
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
        OdontogramId that = (OdontogramId) o;
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