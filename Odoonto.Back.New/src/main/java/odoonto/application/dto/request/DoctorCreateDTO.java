package odoonto.application.dto.request;

/**
 * DTO para la creación de doctores
 */
public class DoctorCreateDTO {
    private String nombreCompleto;
    private String especialidad;
    
    // Constructores
    public DoctorCreateDTO() {}
    
    public DoctorCreateDTO(String nombreCompleto, String especialidad) {
        this.nombreCompleto = nombreCompleto;
        this.especialidad = especialidad;
    }
    
    // Getters y setters
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    
    public String getEspecialidad() {
        return especialidad;
    }
    
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
} 