package odoonto.application.service.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.request.AppointmentCreateDTO;
import odoonto.application.dto.response.AppointmentDTO;
import odoonto.application.mapper.AppointmentMapper;
import odoonto.application.port.in.appointment.AppointmentUpdateUseCase;
import odoonto.application.port.out.ReactiveAppointmentRepository;
import odoonto.application.port.out.ReactiveDoctorRepository;
import odoonto.application.port.out.ReactivePatientRepository;
import odoonto.domain.exceptions.DomainException;

import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para actualizar una cita
 */
@Service
public class AppointmentUpdateService implements AppointmentUpdateUseCase {

    private final ReactiveAppointmentRepository appointmentRepository;
    private final ReactiveDoctorRepository doctorRepository;
    private final ReactivePatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;

    @Autowired
    public AppointmentUpdateService(
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
    public Mono<AppointmentDTO> updateAppointment(String appointmentId, AppointmentCreateDTO appointmentCreateDTO) {
        // Validaciones básicas
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Mono.error(new DomainException("El ID de la cita no puede ser nulo o vacío"));
        }
        
        if (appointmentCreateDTO == null) {
            return Mono.error(new DomainException("Los datos de la cita no pueden ser nulos"));
        }
        
        // Buscar la cita existente
        return appointmentRepository.findById(appointmentId)
            .switchIfEmpty(Mono.error(new DomainException("No existe una cita con el ID: " + appointmentId)))
            .flatMap(existingAppointment -> {
                // Verificación condicional de doctor si cambia
                Mono<Boolean> doctorCheck = Mono.just(true);
                if (appointmentCreateDTO.getDoctorId() != null && 
                    !appointmentCreateDTO.getDoctorId().equals(existingAppointment.getDoctorId())) {
                    
                    doctorCheck = doctorRepository.findById(appointmentCreateDTO.getDoctorId())
                        .switchIfEmpty(Mono.error(new DomainException("No existe un doctor con el ID: " + appointmentCreateDTO.getDoctorId())))
                        .map(doctor -> true);
                }
                
                // Verificación condicional de paciente si cambia
                Mono<Boolean> patientCheck = Mono.just(true);
                if (appointmentCreateDTO.getPatientId() != null && 
                    !appointmentCreateDTO.getPatientId().equals(existingAppointment.getPatientId())) {
                    
                    patientCheck = patientRepository.findById(appointmentCreateDTO.getPatientId())
                        .switchIfEmpty(Mono.error(new DomainException("No existe un paciente con el ID: " + appointmentCreateDTO.getPatientId())))
                        .map(patient -> true);
                }
                
                // Combina ambas verificaciones y luego actualiza
                return Mono.zip(doctorCheck, patientCheck)
                    .flatMap(tuple -> {
                        // Actualizar los campos de la cita usando el mapper
                        appointmentMapper.updateAppointmentFromDTO(appointmentCreateDTO, existingAppointment);
                        
                        // Guardar los cambios
                        return appointmentRepository.save(existingAppointment);
                    })
                    .map(appointmentMapper::toDTO);
            });
    }
} 