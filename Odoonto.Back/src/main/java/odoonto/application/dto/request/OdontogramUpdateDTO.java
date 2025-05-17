package odoonto.application.dto.request;

import java.util.List;
import java.util.Map;

/**
 * DTO para actualizar un odontograma existente
 */
public class OdontogramUpdateDTO {
    
    private String id;
    private String patientId;
    private Map<String, List<LesionCreateDTO>> teethLesions;
    
    /**
     * Constructor por defecto
     */
    public OdontogramUpdateDTO() {
    }
    
    /**
     * Constructor con todos los campos
     * 
     * @param id El identificador del odontograma
     * @param patientId El identificador del paciente asociado
     * @param teethLesions Mapa de lesiones por diente
     */
    public OdontogramUpdateDTO(String id, String patientId, Map<String, List<LesionCreateDTO>> teethLesions) {
        this.id = id;
        this.patientId = patientId;
        this.teethLesions = teethLesions;
    }
    
    /**
     * Obtiene el identificador del odontograma
     * 
     * @return El identificador del odontograma
     */
    public String getId() {
        return id;
    }
    
    /**
     * Establece el identificador del odontograma
     * 
     * @param id El identificador del odontograma
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Obtiene el identificador del paciente asociado
     * 
     * @return El identificador del paciente
     */
    public String getPatientId() {
        return patientId;
    }
    
    /**
     * Establece el identificador del paciente asociado
     * 
     * @param patientId El identificador del paciente
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    /**
     * Obtiene el mapa de lesiones por diente
     * 
     * @return Mapa donde la clave es el número de diente y el valor es una lista de lesiones
     */
    public Map<String, List<LesionCreateDTO>> getTeethLesions() {
        return teethLesions;
    }
    
    /**
     * Establece el mapa de lesiones por diente
     * 
     * @param teethLesions Mapa donde la clave es el número de diente y el valor es una lista de lesiones
     */
    public void setTeethLesions(Map<String, List<LesionCreateDTO>> teethLesions) {
        this.teethLesions = teethLesions;
    }
    
    @Override
    public String toString() {
        return "OdontogramUpdateDTO{" +
                "id='" + id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", teethLesions=" + teethLesions +
                '}';
    }
} 