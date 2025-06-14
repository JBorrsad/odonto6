package odoonto.domain.model.valueobjects;

/**
 * Representa la posición de un diente en la arcada dental.
 * Incluye posiciones para dentición permanente y temporal.
 */
public enum ToothPosition {
    UPPER_RIGHT("Superior derecho", true),
    UPPER_LEFT("Superior izquierdo", true),
    LOWER_LEFT("Inferior izquierdo", true),
    LOWER_RIGHT("Inferior derecho", true),
    UPPER_RIGHT_TEMPORARY("Superior derecho temporal", false),
    UPPER_LEFT_TEMPORARY("Superior izquierdo temporal", false),
    LOWER_LEFT_TEMPORARY("Inferior izquierdo temporal", false),
    LOWER_RIGHT_TEMPORARY("Inferior derecho temporal", false);
    
    private final String descripcion;
    private final boolean isPermanent;
    
    ToothPosition(String descripcion, boolean isPermanent) {
        this.descripcion = descripcion;
        this.isPermanent = isPermanent;
    }
    
    /**
     * Obtiene la descripción legible de la posición
     * @return Descripción en español
     */
    public String getDescripcion() {
        return descripcion;
    }
    
    /**
     * Indica si la posición corresponde a dentición permanente
     * @return true si es dentición permanente
     */
    public boolean isPermanent() {
        return isPermanent;
    }
    
    /**
     * Indica si la posición corresponde a dentición temporal
     * @return true si es dentición temporal
     */
    public boolean isTemporary() {
        return !isPermanent;
    }
} 