package odoonto.application.service.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.request.AppointmentCreateDTO;
import odoonto.application.dto.response.AppointmentDTO;
import odoonto.application.mapper.AppointmentMapper;
import odoonto.application.port.in.appointment.AppointmentCreateUseCase;
import odoonto.application.port.out.ReactiveAppointmentRepository;
import odoonto.application.port.out.ReactiveDoctorRepository;
import odoonto.application.port.out.ReactivePatientRepository;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.aggregates.Appointment;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para crear una cita
 */
@Service
public class AppointmentCreateService implements AppointmentCreateUseCase {

    private final ReactiveAppointmentRepository appointmentRepository;
    private final ReactiveDoctorRepository doctorRepository;
    private final ReactivePatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;

    @Autowired
    public AppointmentCreateService(
            ReactiveAppointmentRepository appointmentRepository,
            ReactiveDoctorRepository doctorRepository,
            ReactivePatientRepository patientRepository,
            AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public Mono<AppointmentDTO> createAppointment(AppointmentCreateDTO appointmentCreateDTO) {
        // Validaciones básicas
        if (appointmentCreateDTO == null) {
            return Mono.error(new DomainException("Los datos de la cita no pueden ser nulos"));
        }
        
        // Verificar que el doctor y el paciente existen
        return Mono.zip(
            doctorRepository.findById(appointmentCreateDTO.getDoctorId())
                .switchIfEmpty(Mono.error(new DomainException("No existe un doctor con el ID: " + appointmentCreateDTO.getDoctorId()))),
            patientRepository.findById(appointmentCreateDTO.getPatientId())
                .switchIfEmpty(Mono.error(new DomainException("No existe un paciente con el ID: " + appointmentCreateDTO.getPatientId())))
        )
        .flatMap(tuple -> {
            // Convertir DTO a entidad de dominio
            Appointment appointment = appointmentMapper.toEntity(appointmentCreateDTO);
            
            // Guardar la cita
            return appointmentRepository.save(appointment);
        })
        .map(appointmentMapper::toDTO);
    }
} 