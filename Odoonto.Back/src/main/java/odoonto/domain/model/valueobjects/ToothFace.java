package odoonto.domain.model.valueobjects;

import odoonto.domain.exceptions.DomainException;

/**
 * Objeto de valor que representa las caras de un diente en el odontograma.
 * Las caras varían según el tipo de diente:
 * - Dientes anteriores (incisivos y caninos): tienen VESTIBULAR, PALATINA/LINGUAL, MESIAL, DISTAL e INCISAL
 * - Dientes posteriores (premolares y molares): tienen VESTIBULAR, PALATINA/LINGUAL, MESIAL, DISTAL y OCLUSAL
 */
public enum ToothFace {
    // Caras principales del diente
    VESTIBULAR("V", "Cara externa o visible del diente"),
    PALATINA("P", "Cara interna hacia el paladar en superiores"),
    LINGUAL("L", "Cara interna hacia la lengua en inferiores"),
    MESIAL("M", "Cara lateral hacia la línea media"),
    DISTAL("D", "Cara lateral alejada de la línea media"),
    OCLUSAL("O", "Superficie de masticación en premolares y molares"),
    INCISAL("I", "Borde cortante en incisivos y caninos"),
    
    // Estado completo del diente
    COMPLETO("C", "Diente completo");
    
    private final String codigo;
    private final String descripcion;
    
    ToothFace(String codigo, String descripcion) {
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
     * Convierte un código de cara a su enumeración
     * @param codigo Código de la cara (V, P, L, M, D, O, I, C)
     * @return La enumeración correspondiente al código
     * @throws DomainException si el código no es válido
     */
    public static ToothFace fromCodigo(String codigo) {
        for (ToothFace face : ToothFace.values()) {
            if (face.getCodigo().equalsIgnoreCase(codigo)) {
                return face;
            }
        }
        throw new DomainException("Código de cara de diente no válido: " + codigo);
    }
    
    /**
     * Verifica si una cara es aplicable para un tipo de diente
     * @param isAnterior True si es diente anterior (incisivo o canino), false si es posterior (premolar o molar)
     * @return True si la cara es aplicable al tipo de diente
     */
    public boolean isApplicable(boolean isAnterior) {
        if (this == COMPLETO) {
            return true;
        }
        
        if (isAnterior) {
            // Para incisivos y caninos no aplica la cara oclusal
            return this != OCLUSAL;
        } else {
            // Para premolares y molares no aplica la cara incisal
            return this != INCISAL;
        }
    }
}