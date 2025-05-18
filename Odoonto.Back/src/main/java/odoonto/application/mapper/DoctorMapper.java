package odoonto.application.mapper;

import odoonto.domain.model.aggregates.Doctor;
import odoonto.application.dto.response.DoctorDTO;
import odoonto.application.dto.request.DoctorCreateDTO;
import odoonto.domain.model.valueobjects.Specialty;

import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Doctor y sus DTOs
 */
@Component
public class DoctorMapper {
    
    /**
     * Convierte un Doctor a DoctorDTO
     */
    public DoctorDTO toDTO(Doctor doctor) {
        if (doctor == null) {
            return null;
        }
        
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setNombreCompleto(doctor.getNombreCompleto());
        dto.setEspecialidad(doctor.getEspecialidad().name());
        return dto;
    }
    
    /**
     * Convierte un DoctorCreateDTO a Doctor
     */
    public Doctor toEntity(DoctorCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        
        // Convertir String a Specialty enum
        Specialty especialidad = Specialty.valueOf(dto.getEspecialidad());
        
        return new Doctor(
            dto.getNombreCompleto(),
            especialidad
        );
    }
    
    /**
     * Actualiza un Doctor existente con datos de un DTO
     */
    public void updateDoctorFromDTO(DoctorCreateDTO dto, Doctor doctor) {
        if (dto == null || doctor == null) {
            return;
        }
        
        if (dto.getNombreCompleto() != null) {
            doctor.setNombreCompleto(dto.getNombreCompleto());
        }
        
        if (dto.getEspecialidad() != null) {
            doctor.setEspecialidad(Specialty.valueOf(dto.getEspecialidad()));
        }
    }
} 