package odoonto.application.dto.request;

/**
 * DTO para la creación de lesiones en el odontograma
 */
public class LesionCreateDTO {
    private String toothId;
    private String face;
    private String lesionType;
    
    // Constructores
    public LesionCreateDTO() {}
    
    public LesionCreateDTO(String toothId, String face, String lesionType) {
        this.toothId = toothId;
        this.face = face;
        this.lesionType = lesionType;
    }
    
    // Getters y setters
    public String getToothId() {
        return toothId;
    }
    
    public void setToothId(String toothId) {
        this.toothId = toothId;
    }
    
    public String getFace() {
        return face;
    }
    
    public void setFace(String face) {
        this.face = face;
    }
    
    public String getLesionType() {
        return lesionType;
    }
    
    public void setLesionType(String lesionType) {
        this.lesionType = lesionType;
    }
} 