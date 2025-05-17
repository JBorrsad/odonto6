package odoonto.api.presentation.dto.mappers;

import org.springframework.stereotype.Component;
import odoonto.api.domain.models.Doctor;
import odoonto.api.presentation.dto.CreateDoctorDto;
import odoonto.api.presentation.dto.DoctorDto;

@Component
public class DoctorMapper {

    /** Para respuesta: Doctor → DoctorDto */
    public DoctorDto toDto(Doctor entity) {
        if (entity == null) return null;
        return new DoctorDto(
                entity.getId(),
                entity.getNombreCompleto(),
                entity.getEspecialidad()
        );
    }

    /** Para creación: CreateDoctorDto → Doctor (genera id internamente) */
    public Doctor toEntity(CreateDoctorDto dto) {
        if (dto == null) return null;
        return new Doctor(dto.getNombreCompleto(), dto.getEspecialidad());
    }

    /** Para actualización: DoctorDto → Doctor (conserva id si viene) */
    public Doctor toEntity(DoctorDto dto) {
        if (dto == null) return null;
        Doctor doc = new Doctor(dto.getNombreCompleto(), dto.getEspecialidad());
        // si vienen id (PUT), lo asignamos aquí
        if (dto.getId() != null) {
            doc.setId(dto.getId());
        }
        return doc;
    }
}
