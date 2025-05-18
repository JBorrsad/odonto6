package odoonto.application.service.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.response.AppointmentDTO;
import odoonto.application.mapper.AppointmentMapper;
import odoonto.application.port.in.appointment.AppointmentQueryUseCase;
import odoonto.application.port.out.ReactiveAppointmentRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para consultar citas
 */
@Service
public class AppointmentQueryService implements AppointmentQueryUseCase {

    private final ReactiveAppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    @Autowired
    public AppointmentQueryService(
            ReactiveAppointmentRepository appointmentRepository,
            AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public Mono<AppointmentDTO> findById(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Mono.empty();
        }
        
        return appointmentRepository.findById(appointmentId)
                .map(appointmentMapper::toDTO);
    }

    @Override
    public Flux<AppointmentDTO> findAll() {
        return appointmentRepository.findAll()
            .map(appointmentMapper::toDTO);
    }

    @Override
    public Flux<AppointmentDTO> findByPatientId(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            return Flux.empty();
        }
        
        return appointmentRepository.findByPatientId(patientId)
            .map(appointmentMapper::toDTO);
    }

    @Override
    public Flux<AppointmentDTO> findByDoctorId(String doctorId) {
        if (doctorId == null || doctorId.trim().isEmpty()) {
            return Flux.empty();
        }
        
        return appointmentRepository.findByDoctorId(doctorId)
            .map(appointmentMapper::toDTO);
    }
} 