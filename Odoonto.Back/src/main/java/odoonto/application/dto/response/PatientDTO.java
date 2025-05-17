package odoonto.application.dto.response;

import java.time.LocalDate;

/**
 * DTO de respuesta para Patient (Paciente)
 */
public class PatientDTO {
    private String id;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String sexo;
    private String email;
    private String telefono;
    private AddressDTO direccion;
    
    // Constructores
    public PatientDTO() {
    }
    
    public PatientDTO(String id, String nombre, String apellido, LocalDate fechaNacimiento, 
                     String sexo, String email, String telefono, AddressDTO direccion) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
    }
    
    // Getters y setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
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
     * Clase para representar una dirección
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
        
        public AddressDTO(String calle, String numero, String colonia, String codigoPostal, 
                         String ciudad, String estado, String pais) {
            this.calle = calle;
            this.numero = numero;
            this.colonia = colonia;
            this.codigoPostal = codigoPostal;
            this.ciudad = ciudad;
            this.estado = estado;
            this.pais = pais;
        }
        
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