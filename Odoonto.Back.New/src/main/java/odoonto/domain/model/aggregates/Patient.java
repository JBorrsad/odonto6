package odoonto.domain.model.aggregates;

import odoonto.domain.model.valueobjects.EmailAddress;
import odoonto.domain.model.valueobjects.PhoneNumber;
import odoonto.domain.model.valueobjects.Sexo;
import odoonto.domain.exceptions.DomainException;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Agregado raíz que representa un paciente en el sistema.
 * Contiene el odontograma como parte del agregado.
 */
public class Patient {
    private String id;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private Sexo sexo;
    private PhoneNumber telefono;
    private EmailAddress email;
    private Odontogram odontogram;
    private List<String> appointmentIds;
    private String medicalRecordId;
    private LocalDate lastVisitDate;

    /**
     * Constructor sin argumentos necesario para frameworks
     */
    public Patient() {
        this.odontogram = new Odontogram();
        this.appointmentIds = new ArrayList<>();
    }
    
    /**
     * Constructor para crear un nuevo paciente
     */
    public Patient(String nombre, String apellido, LocalDate fechaNacimiento, 
                  Sexo sexo, PhoneNumber telefono, EmailAddress email) {
        validateBasicInfo(nombre, apellido, fechaNacimiento);
        
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.telefono = telefono;
        this.email = email;
        this.odontogram = new Odontogram();
        this.appointmentIds = new ArrayList<>();
    }
    
    /**
     * Constructor con ID para reconstrucción desde persistencia
     */
    public Patient(String id, String nombre, String apellido, LocalDate fechaNacimiento,
                  Sexo sexo, PhoneNumber telefono, EmailAddress email, Odontogram odontogram,
                  List<String> appointmentIds, String medicalRecordId, LocalDate lastVisitDate) {
        this(nombre, apellido, fechaNacimiento, sexo, telefono, email);
        
        if (id == null || id.trim().isEmpty()) {
            throw new DomainException("El ID del paciente no puede estar vacío");
        }
        
        this.id = id;
        if (odontogram != null) {
            this.odontogram = odontogram;
        }
        this.appointmentIds = appointmentIds != null ? new ArrayList<>(appointmentIds) : new ArrayList<>();
        this.medicalRecordId = medicalRecordId;
        this.lastVisitDate = lastVisitDate;
    }
    
    /**
     * Valida información básica del paciente
     */
    private void validateBasicInfo(String nombre, String apellido, LocalDate fechaNacimiento) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DomainException("El nombre del paciente no puede estar vacío");
        }
        
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new DomainException("El apellido del paciente no puede estar vacío");
        }
        
        if (fechaNacimiento == null) {
            throw new DomainException("La fecha de nacimiento no puede estar vacía");
        }
        
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new DomainException("La fecha de nacimiento no puede ser futura");
        }
    }
    
    /**
     * Calcula la edad del paciente en años
     * @return Edad en años
     */
    public int getEdad() {
        if (fechaNacimiento == null) {
            return 0;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
    
    /**
     * Actualiza el odontograma del paciente
     * @param odontogram Nuevo odontograma
     */
    public void updateOdontogram(Odontogram odontogram) {
        if (odontogram == null) {
            throw new DomainException("El odontograma no puede ser nulo");
        }
        this.odontogram = odontogram;
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
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DomainException("El nombre del paciente no puede estar vacío");
        }
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new DomainException("El apellido del paciente no puede estar vacío");
        }
        this.apellido = apellido;
    }
    
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            throw new DomainException("La fecha de nacimiento no puede estar vacía");
        }
        
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new DomainException("La fecha de nacimiento no puede ser futura");
        }
        
        this.fechaNacimiento = fechaNacimiento;
    }
    
    public Sexo getSexo() {
        return sexo;
    }
    
    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }
    
    public PhoneNumber getTelefono() {
        return telefono;
    }
    
    public void setTelefono(PhoneNumber telefono) {
        this.telefono = telefono;
    }
    
    public EmailAddress getEmail() {
        return email;
    }
    
    public void setEmail(EmailAddress email) {
        this.email = email;
    }
    
    public Odontogram getOdontogram() {
        return odontogram;
    }
    
    public void setOdontogram(Odontogram odontogram) {
        this.odontogram = odontogram != null ? odontogram : new Odontogram();
    }
    
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
    
    public List<String> getAppointmentIds() {
        return Collections.unmodifiableList(appointmentIds);
    }
    
    public void setAppointmentIds(List<String> appointmentIds) {
        this.appointmentIds = appointmentIds != null ? new ArrayList<>(appointmentIds) : new ArrayList<>();
    }
    
    public void addAppointmentId(String appointmentId) {
        if (appointmentId != null && !appointmentId.trim().isEmpty()) {
            this.appointmentIds.add(appointmentId);
        }
    }
    
    public void removeAppointmentId(String appointmentId) {
        this.appointmentIds.remove(appointmentId);
    }
    
    public String getMedicalRecordId() {
        return medicalRecordId;
    }
    
    public void setMedicalRecordId(String medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }
    
    public LocalDate getLastVisitDate() {
        return lastVisitDate;
    }
    
    public void setLastVisitDate(LocalDate lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Patient patient = (Patient) o;
        return id != null && id.equals(patient.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
