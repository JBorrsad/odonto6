package odoonto.application.service.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.response.AppointmentDTO;
import odoonto.application.mapper.AppointmentMapper;
import odoonto.application.port.in.appointment.AppointmentQueryUseCase;
import odoonto.domain.repository.AppointmentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del caso de uso para consultar citas
 */
@Service
public class AppointmentQueryService implements AppointmentQueryUseCase {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    @Autowired
    public AppointmentQueryService(
            AppointmentRepository appointmentRepository,
            AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public Optional<AppointmentDTO> findById(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return appointmentRepository.findById(appointmentId)
                .map(appointmentMapper::toDTO);
    }

    @Override
    public List<AppointmentDTO> findAll() {
        return appointmentRepository.findAll().stream()
            .map(appointmentMapper::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDTO> findByPatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return List.of();
        }
        
        return appointmentRepository.findByPatientId(patientId).stream()
            .map(appointmentMapper::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDTO> findByDoctorId(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            return List.of();
        }
        
        return appointmentRepository.findByDoctorId(doctorId).stream()
            .map(appointmentMapper::toDTO)
            .collect(Collectors.toList());
    }
} 