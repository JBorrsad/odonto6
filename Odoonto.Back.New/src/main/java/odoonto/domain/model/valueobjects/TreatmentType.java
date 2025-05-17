package odoonto.domain.model.valueobjects;

/**
 * Enumeración que representa los diferentes tipos de tratamientos dentales.
 */
public enum TreatmentType {
    
    // Tratamientos preventivos
    LIMPIEZA("Limpieza dental", "Profilaxis dental para eliminar placa y sarro", Categoria.PREVENTIVO),
    SELLADOR("Sellador de fosas y fisuras", "Aplicación de sellador para prevenir caries", Categoria.PREVENTIVO),
    FLUORACION("Aplicación de flúor", "Tratamiento con flúor para fortalecer el esmalte", Categoria.PREVENTIVO),
    HIGIENE_ORAL("Instrucción de higiene oral", "Enseñanza de técnicas de cepillado y uso de hilo dental", Categoria.PREVENTIVO),
    
    // Tratamientos restauradores
    OBTURACION_RESINA("Obturación con resina", "Relleno de composite para restaurar un diente con caries", Categoria.RESTAURADOR),
    OBTURACION_AMALGAMA("Obturación con amalgama", "Relleno de amalgama para restaurar un diente con caries", Categoria.RESTAURADOR),
    INCRUSTACION("Incrustación", "Restauración indirecta para dientes posteriores", Categoria.RESTAURADOR),
    CORONA("Corona dental", "Restauración que cubre toda la superficie del diente", Categoria.RESTAURADOR),
    
    // Tratamientos endodónticos
    ENDODONCIA_UNIRRADICULAR("Endodoncia unirradicular", "Tratamiento de conducto en diente con una raíz", Categoria.ENDODONTICO),
    ENDODONCIA_BIRRADICULAR("Endodoncia birradicular", "Tratamiento de conducto en diente con dos raíces", Categoria.ENDODONTICO),
    ENDODONCIA_MULTIRRADICULAR("Endodoncia multirradicular", "Tratamiento de conducto en diente con tres o más raíces", Categoria.ENDODONTICO),
    APICECTOMIA("Apicectomía", "Extirpación quirúrgica del ápice radicular", Categoria.ENDODONTICO),
    
    // Tratamientos periodontales
    CURETAJE("Curetaje", "Limpieza profunda por debajo de la línea de las encías", Categoria.PERIODONTAL),
    GINGIVECTOMIA("Gingivectomía", "Extirpación quirúrgica de tejido gingival", Categoria.PERIODONTAL),
    CIRUGIA_PERIODONTAL("Cirugía periodontal", "Intervención quirúrgica para tratar enfermedad periodontal", Categoria.PERIODONTAL),
    FERULIZACION("Ferulización", "Fijación de dientes con movilidad", Categoria.PERIODONTAL),
    
    // Tratamientos quirúrgicos
    EXTRACCION_SIMPLE("Extracción simple", "Extracción de diente visible", Categoria.QUIRURGICO),
    EXTRACCION_COMPLEJA("Extracción compleja", "Extracción quirúrgica", Categoria.QUIRURGICO),
    EXTRACCION_CORDAL("Extracción de cordal", "Extracción de muela del juicio", Categoria.QUIRURGICO),
    CIRUGIA_PREPROTESICA("Cirugía preprotésica", "Preparación quirúrgica para prótesis", Categoria.QUIRURGICO),
    
    // Tratamientos protésicos
    PROTESIS_REMOVIBLE("Prótesis removible", "Dentadura parcial o completa removible", Categoria.PROTESICO),
    PROTESIS_FIJA("Prótesis fija", "Puente dental fijo", Categoria.PROTESICO),
    IMPLANTE("Implante dental", "Colocación de implante de titanio", Categoria.PROTESICO),
    CORONA_SOBRE_IMPLANTE("Corona sobre implante", "Restauración sobre implante dental", Categoria.PROTESICO),
    
    // Tratamientos estéticos
    BLANQUEAMIENTO("Blanqueamiento dental", "Procedimiento para aclarar el color del diente", Categoria.ESTETICO),
    CARILLAS("Carillas dentales", "Láminas que se adhieren a la cara visible del diente", Categoria.ESTETICO),
    MICROABRASION("Microabrasión", "Técnica para eliminar manchas superficiales", Categoria.ESTETICO),
    RECONSTRUCCION("Reconstrucción estética", "Restauración completa de la estética dental", Categoria.ESTETICO),
    
    // Tratamientos ortodónticos
    ORTODONCIA_CONVENCIONAL("Ortodoncia convencional", "Brackets metálicos tradicionales", Categoria.ORTODONTICO),
    ORTODONCIA_ESTETICA("Ortodoncia estética", "Brackets cerámicos o de zafiro", Categoria.ORTODONTICO),
    ORTODONCIA_INVISIBLE("Ortodoncia invisible", "Alineadores transparentes", Categoria.ORTODONTICO),
    RETENEDORES("Retenedores", "Aparatos para mantener los dientes en posición", Categoria.ORTODONTICO),
    
    // Otros tratamientos
    RADIOGRAFIA("Radiografía dental", "Imagen diagnóstica", Categoria.DIAGNOSTICO),
    AJUSTE_OCLUSAL("Ajuste oclusal", "Corrección de la mordida", Categoria.OCLUSION),
    PROTECTOR_BUCAL("Protector bucal", "Dispositivo de protección para deportes", Categoria.PREVENTIVO),
    TRATAMIENTO_ATM("Tratamiento ATM", "Terapia para trastorno de la articulación temporomandibular", Categoria.OCLUSION);
    
    private final String nombre;
    private final String descripcion;
    private final Categoria categoria;
    
    TreatmentType(String nombre, String descripcion, Categoria categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public Categoria getCategoria() {
        return categoria;
    }
    
    /**
     * Verifica si el tratamiento es un tratamiento preventivo
     * @return true si es preventivo
     */
    public boolean isPreventivo() {
        return this.categoria == Categoria.PREVENTIVO;
    }
    
    /**
     * Verifica si el tratamiento es un tratamiento restaurador
     * @return true si es restaurador
     */
    public boolean isRestaurador() {
        return this.categoria == Categoria.RESTAURADOR;
    }
    
    /**
     * Verifica si el tratamiento es un tratamiento quirúrgico
     * @return true si es quirúrgico
     */
    public boolean isQuirurgico() {
        return this.categoria == Categoria.QUIRURGICO;
    }
    
    /**
     * Obtiene un TreatmentType a partir de su nombre
     * @param nombre Nombre del tratamiento
     * @return TreatmentType correspondiente o null si no existe
     */
    public static TreatmentType fromNombre(String nombre) {
        if (nombre == null) return null;
        
        for (TreatmentType type : values()) {
            if (type.getNombre().equalsIgnoreCase(nombre.trim())) {
                return type;
            }
        }
        
        return null;
    }
    
    /**
     * Enumeración para categorizar los tipos de tratamientos
     */
    public enum Categoria {
        PREVENTIVO("Preventivo"),
        RESTAURADOR("Restaurador"),
        ENDODONTICO("Endodóntico"),
        PERIODONTAL("Periodontal"),
        QUIRURGICO("Quirúrgico"),
        PROTESICO("Protésico"),
        ESTETICO("Estético"),
        ORTODONTICO("Ortodóntico"),
        DIAGNOSTICO("Diagnóstico"),
        OCLUSION("Oclusión");
        
        private final String nombre;
        
        Categoria(String nombre) {
            this.nombre = nombre;
        }
        
        public String getNombre() {
            return nombre;
        }
    }
} 