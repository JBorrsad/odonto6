package odoonto.application.port.in.doctor;

import odoonto.application.dto.response.DoctorDTO;
import java.util.List;
import java.util.Optional;

/**
 * Caso de uso para consultar doctores
 */
public interface DoctorQueryUseCase {
    Optional<DoctorDTO> findById(String doctorId);
    List<DoctorDTO> findAll();
    List<DoctorDTO> findByEspecialidad(String especialidad);
    List<DoctorDTO> findByNombre(String nombre);
} 