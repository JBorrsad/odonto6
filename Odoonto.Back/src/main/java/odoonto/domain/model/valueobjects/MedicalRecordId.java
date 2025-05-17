package odoonto.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

/**
 * Objeto de valor que representa el identificador único de un historial médico.
 * Es inmutable y puede derivarse de un PatientId o especificarse directamente.
 */
public final class MedicalRecordId {
    private final String value;
    
    /**
     * Constructor privado para inicializar el valor
     * @param value Valor del identificador
     */
    private MedicalRecordId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador del historial médico no puede estar vacío");
        }
        this.value = value;
    }
    
    /**
     * Crea un nuevo MedicalRecordId a partir de un valor existente
     * @param value Valor existente
     * @return Nuevo MedicalRecordId
     */
    public static MedicalRecordId of(String value) {
        return new MedicalRecordId(value);
    }
    
    /**
     * Genera un nuevo MedicalRecordId aleatorio usando UUID
     * @return Nuevo MedicalRecordId aleatorio
     */
    public static MedicalRecordId generate() {
        return new MedicalRecordId(UUID.randomUUID().toString());
    }
    
    /**
     * Crea un nuevo MedicalRecordId derivado a partir de un PatientId
     * @param patientId Identificador del paciente asociado
     * @return Nuevo MedicalRecordId
     */
    public static MedicalRecordId fromPatientId(PatientId patientId) {
        if (patientId == null) {
            throw new IllegalArgumentException("El identificador del paciente no puede ser nulo");
        }
        return new MedicalRecordId("medical_record_" + patientId.getValue());
    }
    
    /**
     * Extrae el identificador del paciente si este MedicalRecordId fue creado a partir de un PatientId
     * @return PatientId extraído o null si no fue derivado de un paciente
     */
    public PatientId extractPatientId() {
        if (value.startsWith("medical_record_")) {
            String patientIdValue = value.substring("medical_record_".length());
            return PatientId.of(patientIdValue);
        }
        return null;
    }
    
    /**
     * Verifica si este MedicalRecordId fue derivado de un PatientId
     * @return true si fue derivado, false en caso contrario
     */
    public boolean isDerivedFromPatient() {
        return value.startsWith("medical_record_");
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
        MedicalRecordId that = (MedicalRecordId) o;
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