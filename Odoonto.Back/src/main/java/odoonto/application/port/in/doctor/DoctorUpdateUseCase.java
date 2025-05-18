package odoonto.application.port.in.doctor;

import odoonto.application.dto.request.DoctorCreateDTO;
import odoonto.application.dto.response.DoctorDTO;

/**
 * Caso de uso para actualizar un doctor
 */
public interface DoctorUpdateUseCase {
    DoctorDTO updateDoctor(String doctorId, DoctorCreateDTO doctorCreateDTO);
} 