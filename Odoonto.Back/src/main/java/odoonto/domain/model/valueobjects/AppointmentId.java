package odoonto.domain.model.valueobjects;

import java.util.Objects;
import java.util.UUID;

import odoonto.domain.exceptions.DomainException;

/**
 * Objeto de valor que representa el identificador único de una cita médica.
 * Este objeto de valor es inmutable.
 */
public class AppointmentId {
    private final String value;

    /**
     * Constructor privado. Utilizar los métodos factory para crear instancias.
     * 
     * @param value Valor del identificador
     */
    private AppointmentId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("El identificador de la cita no puede ser nulo o vacío");
        }
        this.value = value;
    }

    /**
     * Crea un nuevo identificador de cita basado en un UUID aleatorio
     * 
     * @return Un nuevo identificador de cita
     */
    public static AppointmentId generate() {
        return new AppointmentId("appointment_" + UUID.randomUUID().toString());
    }

    /**
     * Crea un identificador de cita a partir de un valor existente
     * 
     * @param id El identificador como String
     * @return El objeto AppointmentId
     */
    public static AppointmentId of(String id) {
        return new AppointmentId(id);
    }

    /**
     * Crea un identificador de cita derivado del ID de paciente y doctor
     * 
     * @param patientId El ID del paciente
     * @param doctorId El ID del doctor
     * @param timestamp El timestamp de la cita
     * @return El objeto AppointmentId
     */
    public static AppointmentId fromPatientAndDoctor(PatientId patientId, String doctorId, long timestamp) {
        return new AppointmentId(
            "appointment_" + patientId.getValue() + "_" + doctorId + "_" + timestamp
        );
    }

    /**
     * Retorna el valor del identificador como String
     * 
     * @return El valor del identificador
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentId that = (AppointmentId) o;
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