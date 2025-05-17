package odoonto.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO para solicitud de creación de paciente
 */
public class PatientCreateDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String apellido;
    
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;
    
    @NotBlank(message = "El sexo es obligatorio")
    @Pattern(regexp = "^(MASCULINO|FEMENINO|OTRO)$", message = "El sexo debe ser MASCULINO, FEMENINO u OTRO")
    private String sexo;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9\\+\\-\\s]{8,15}$", message = "El teléfono debe tener entre 8 y 15 dígitos")
    private String telefono;
    
    private AddressDTO direccion;
    
    // Constructores
    public PatientCreateDTO() {
    }
    
    // Getters y setters
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    
    public String getSexo() {
        return sexo;
    }
    
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public AddressDTO getDireccion() {
        return direccion;
    }
    
    public void setDireccion(AddressDTO direccion) {
        this.direccion = direccion;
    }
    
    /**
     * DTO para dirección en la creación de paciente
     */
    public static class AddressDTO {
        private String calle;
        private String numero;
        private String colonia;
        private String codigoPostal;
        private String ciudad;
        private String estado;
        private String pais;
        
        public AddressDTO() {
        }
        
        // Getters y setters
        public String getCalle() {
            return calle;
        }
        
        public void setCalle(String calle) {
            this.calle = calle;
        }
        
        public String getNumero() {
            return numero;
        }
        
        public void setNumero(String numero) {
            this.numero = numero;
        }
        
        public String getColonia() {
            return colonia;
        }
        
        public void setColonia(String colonia) {
            this.colonia = colonia;
        }
        
        public String getCodigoPostal() {
            return codigoPostal;
        }
        
        public void setCodigoPostal(String codigoPostal) {
            this.codigoPostal = codigoPostal;
        }
        
        public String getCiudad() {
            return ciudad;
        }
        
        public void setCiudad(String ciudad) {
            this.ciudad = ciudad;
        }
        
        public String getEstado() {
            return estado;
        }
        
        public void setEstado(String estado) {
            this.estado = estado;
        }
        
        public String getPais() {
            return pais;
        }
        
        public void setPais(String pais) {
            this.pais = pais;
        }
    }
} 