package odoonto.domain.model.valueobjects;

/**
 * Objeto de valor que representa el sexo de un paciente
 */
public enum Sexo {
    MASCULINO("M"),
    FEMENINO("F"),
    OTRO("O");
    
    private final String codigo;
    
    Sexo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    /**
     * Convierte un código de sexo a su enumeración
     * @param codigo Código del sexo ('M', 'F', 'O')
     * @return La enumeración correspondiente al código
     * @throws IllegalArgumentException si el código no es válido
     */
    public static Sexo fromCodigo(String codigo) {
        for (Sexo sexo : Sexo.values()) {
            if (sexo.getCodigo().equalsIgnoreCase(codigo)) {
                return sexo;
            }
        }
        throw new IllegalArgumentException("Código de sexo no válido: " + codigo);
    }
}