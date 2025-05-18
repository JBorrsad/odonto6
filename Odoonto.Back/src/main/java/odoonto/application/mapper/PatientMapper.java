package odoonto.application.mapper;

import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.model.aggregates.Odontogram;
import odoonto.application.dto.response.PatientDTO;
import odoonto.application.dto.request.PatientCreateDTO;
import odoonto.domain.model.valueobjects.EmailAddress;
import odoonto.domain.model.valueobjects.PhoneNumber;
import odoonto.domain.model.valueobjects.Sexo;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

import org.springframework.stereotype.Component;

/**
 * Mapeador para convertir entre DTOs y entidades de Patient
 * 
 * Nota: El odontograma es un objeto de valor que forma parte del agregado Patient.
 * No requiere un ID independiente ya que sigue el ciclo de vida del paciente.
 */
@Component
public class PatientMapper {
    
    /**
     * Convierte una entidad de dominio a un DTO de respuesta
     */
    public PatientDTO toDTO(Patient patient) {
        if (patient == null) {
            return null;
        }
        
        return new PatientDTO(
            patient.getId(),
            patient.getNombre(),
            patient.getApellido(),
            patient.getFechaNacimiento(),
            patient.getSexo().toString(),
            patient.getTelefono().getValue(),
            patient.getEmail().getValue(),
            patient.getId()
        );
    }
    
    /**
     * Convierte un DTO de creación a una entidad de dominio
     */
    public Patient toEntity(PatientCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Sexo sexo = Sexo.valueOf(dto.getSexo());
        PhoneNumber telefono = new PhoneNumber(dto.getTelefono());
        EmailAddress email = new EmailAddress(dto.getEmail());
        Instant fechaNacimiento = Instant.parse(dto.getFechaNacimiento());
        
        // Calculamos la edad basada en la fecha de nacimiento
        int edad = Period.between(
            LocalDate.ofInstant(fechaNacimiento, ZoneId.systemDefault()),
            LocalDate.now()
        ).getYears();
        
        // Creamos un nuevo paciente con su odontograma
        return new Patient(
            dto.getNombre(), 
            dto.getApellido(), 
            fechaNacimiento, 
            sexo, 
            telefono, 
            email, 
            edad
        );
    }
    
    /**
     * Actualiza una entidad existente con datos de un DTO
     */
    public void updatePatientFromDTO(PatientCreateDTO dto, Patient patient) {
        if (dto == null || patient == null) {
            return;
        }
        
        if (dto.getNombre() != null) {
            patient.setNombre(dto.getNombre());
        }
        
        if (dto.getApellido() != null) {
            patient.setApellido(dto.getApellido());
        }
        
        if (dto.getSexo() != null) {
            patient.setSexo(Sexo.valueOf(dto.getSexo()));
        }
        
        if (dto.getTelefono() != null) {
            patient.setTelefono(new PhoneNumber(dto.getTelefono()));
        }
        
        if (dto.getEmail() != null) {
            patient.setEmail(new EmailAddress(dto.getEmail()));
        }
        
        if (dto.getFechaNacimiento() != null) {
            Instant fechaNacimiento = Instant.parse(dto.getFechaNacimiento());
            patient.setFechaNacimiento(fechaNacimiento.toString());
            
            // Actualizar también la edad
            int edad = Period.between(
                LocalDate.ofInstant(fechaNacimiento, ZoneId.systemDefault()),
                LocalDate.now()
            ).getYears();
            patient.setAge(edad);
        }
    }
} 