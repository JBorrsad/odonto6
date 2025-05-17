package odoonto.domain.model.valueobjects;

/**
 * Objeto de valor que representa los tipos de lesiones dentales
 */
public enum LesionType {
    // Lesiones comunes
    CARIES("CAR", "Caries dental"),
    FRACTURA("FRA", "Fractura dental"),
    DESGASTE("DES", "Desgaste o erosión"),
    RESTAURACION("RES", "Restauración o empaste"),
    AUSENTE("AUS", "Diente ausente o extraído"),
    CORONA("COR", "Corona dental"),
    ENDODONCIA("END", "Tratamiento de conducto"),
    IMPLANTE("IMP", "Implante dental"),
    SELLANTE("SEL", "Sellante preventivo"),
    
    // Estado normal
    SANO("SAN", "Diente sano sin lesiones");
    
    private final String codigo;
    private final String descripcion;
    
    LesionType(String codigo, String descripcion) {
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
     * Convierte un código de lesión a su enumeración
     * @param codigo Código de la lesión
     * @return La enumeración correspondiente al código
     * @throws IllegalArgumentException si el código no es válido
     */
    public static LesionType fromCodigo(String codigo) {
        for (LesionType type : LesionType.values()) {
            if (type.getCodigo().equalsIgnoreCase(codigo)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Código de lesión no válido: " + codigo);
    }
    
    /**
     * Determina si la lesión implica la pérdida del diente
     * @return True si el diente está ausente
     */
    public boolean isDienteAusente() {
        return this == AUSENTE;
    }
    
    /**
     * Determina si la lesión es un tratamiento restaurativo
     * @return True si es un tratamiento restaurativo
     */
    public boolean isTratamientoRestaurativo() {
        return this == RESTAURACION || this == CORONA || 
               this == ENDODONCIA || this == IMPLANTE || this == SELLANTE;
    }
}