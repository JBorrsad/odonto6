package odoonto.application.port.in.doctor;

import odoonto.application.dto.request.DoctorCreateDTO;
import odoonto.application.dto.response.DoctorDTO;

/**
 * Caso de uso para crear un doctor
 */
public interface DoctorCreateUseCase {
    DoctorDTO createDoctor(DoctorCreateDTO doctorCreateDTO);
} 