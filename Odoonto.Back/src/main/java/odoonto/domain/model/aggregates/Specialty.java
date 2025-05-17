package odoonto.domain.model.aggregates;

/**
 * Enumeración que representa las diferentes especialidades odontológicas.
 */
public enum Specialty {
    
    ODONTOLOGIA_GENERAL("Odontología General", "Diagnóstico, prevención y tratamiento de enfermedades bucales básicas"),
    ENDODONCIA("Endodoncia", "Tratamiento del interior del diente (nervio dental y pulpa)"),
    PERIODONCIA("Periodoncia", "Tratamiento de las encías y tejidos que soportan los dientes"),
    PROSTODONCIA("Prostodoncia", "Rehabilitación de la función oral mediante prótesis dentales"),
    IMPLANTOLOGIA("Implantología", "Colocación de implantes dentales para reemplazar dientes perdidos"),
    ORTODONCIA("Ortodoncia", "Corrección de la posición de los dientes y maxilares"),
    ODONTOPEDIATRIA("Odontopediatría", "Atención dental a niños y adolescentes"),
    CIRUGIA_ORAL("Cirugía Oral", "Procedimientos quirúrgicos en la boca y mandíbula"),
    CIRUGIA_MAXILOFACIAL("Cirugía Maxilofacial", "Cirugía compleja de cara, mandíbula y cuello"),
    PATOLOGIA_ORAL("Patología Oral", "Diagnóstico y tratamiento de enfermedades de la mucosa oral"),
    RADIOLOGIA_ORAL("Radiología Oral", "Diagnóstico por imágenes de estructuras orales y maxilofaciales"),
    ODONTOLOGIA_ESTETICA("Odontología Estética", "Procedimientos enfocados en mejorar la estética dental"),
    ODONTOLOGIA_FORENSE("Odontología Forense", "Aplicación de la odontología en procesos legales e identificación"),
    ODONTOLOGIA_PREVENTIVA("Odontología Preventiva", "Enfoque en prevención de enfermedades bucodentales"),
    ODONTOGERIATRIA("Odontogeriatría", "Atención dental especializada para adultos mayores");
    
    private final String nombre;
    private final String descripcion;
    
    Specialty(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    /**
     * Determina si esta especialidad puede atender a niños
     * @return true si la especialidad puede atender a niños
     */
    public boolean puedeAtenderNinos() {
        return this == ODONTOLOGIA_GENERAL || 
               this == ODONTOPEDIATRIA || 
               this == ORTODONCIA || 
               this == ODONTOLOGIA_PREVENTIVA;
    }
    
    /**
     * Determina si esta especialidad puede realizar procedimientos quirúrgicos
     * @return true si la especialidad puede realizar procedimientos quirúrgicos
     */
    public boolean realizaProcedimientosQuirurgicos() {
        return this == CIRUGIA_ORAL || 
               this == CIRUGIA_MAXILOFACIAL || 
               this == IMPLANTOLOGIA || 
               this == PERIODONCIA;
    }
    
    /**
     * Obtiene una Specialty a partir de su nombre
     * @param nombre Nombre de la especialidad
     * @return Specialty correspondiente o ODONTOLOGIA_GENERAL por defecto
     */
    public static Specialty fromNombre(String nombre) {
        if (nombre == null) {
            return ODONTOLOGIA_GENERAL;
        }
        
        for (Specialty specialty : values()) {
            if (specialty.getNombre().equalsIgnoreCase(nombre.trim())) {
                return specialty;
            }
        }
        
        return ODONTOLOGIA_GENERAL;
    }
    
    @Override
    public String toString() {
        return nombre;
    }
} 