package odoonto.domain.model.valueobjects;

/**
 * Objeto de valor que representa los estados posibles de una cita
 */
public enum AppointmentStatus {
    PENDIENTE("PEND", "Cita programada pendiente"),
    CONFIRMADA("CONF", "Cita confirmada por el paciente"),
    EN_PROCESO("PROC", "Cita en proceso de atención"),
    COMPLETADA("COMP", "Cita completada exitosamente"),
    CANCELADA("CANC", "Cita cancelada"),
    REPROGRAMADA("REPR", "Cita reprogramada para otra fecha");
    
    private final String codigo;
    private final String descripcion;
    
    AppointmentStatus(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    /**
     * Convierte un código de estado a su enumeración
     * @param codigo Código del estado
     * @return La enumeración correspondiente al código
     * @throws IllegalArgumentException si el código no es válido
     */
    public static AppointmentStatus fromCodigo(String codigo) {
        for (AppointmentStatus status : AppointmentStatus.values()) {
            if (status.getCodigo().equalsIgnoreCase(codigo)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Código de estado de cita no válido: " + codigo);
    }
    
    /**
     * Determina si una cita puede ser modificada según su estado
     * @return True si la cita puede ser modificada
     */
    public boolean isModificable() {
        return this == PENDIENTE || this == CONFIRMADA;
    }
    
    /**
     * Determina si una cita puede ser cancelada según su estado
     * @return True si la cita puede ser cancelada
     */
    public boolean isCancelable() {
        return this == PENDIENTE || this == CONFIRMADA;
    }
    
    /**
     * Determina si la cita está activa (no cancelada ni completada)
     * @return True si la cita está activa
     */
    public boolean isActiva() {
        return this == PENDIENTE || this == CONFIRMADA || this == EN_PROCESO;
    }
}