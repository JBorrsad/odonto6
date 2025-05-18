package odoonto.application.mapper;

import odoonto.domain.model.aggregates.Patient;
import odoonto.application.dto.response.PatientDTO;
import odoonto.application.dto.request.PatientCreateDTO;
import odoonto.domain.model.valueobjects.EmailAddress;
import odoonto.domain.model.valueobjects.PhoneNumber;
import odoonto.domain.model.valueobjects.Sexo;

import java.time.LocalDate;


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
        
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId().toString());
        dto.setNombre(patient.getNombre());
        dto.setApellido(patient.getApellido());
        dto.setFechaNacimiento(patient.getFechaNacimiento());
        dto.setSexo(patient.getSexo().toString());
        dto.setTelefono(patient.getTelefono().getValue());
        dto.setEmail(patient.getEmail().getValue());
        
        return dto;
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
        
        // Obtener fecha de nacimiento
        LocalDate fechaNacimiento = dto.getFechaNacimiento();
        
        // Creamos un nuevo paciente
        return new Patient(
            dto.getNombre(), 
            dto.getApellido(), 
            fechaNacimiento, 
            sexo, 
            telefono, 
            email
        );
    }
    
    /**
     * Actualiza una entidad existente con datos de un DTO
     */
    public Patient updateEntityFromDTO(PatientCreateDTO dto, Patient patient) {
        if (dto == null || patient == null) {
            return patient;
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
            patient.setFechaNacimiento(dto.getFechaNacimiento());
        }
        
        return patient;
    }
} 