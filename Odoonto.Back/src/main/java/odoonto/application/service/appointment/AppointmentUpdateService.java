package odoonto.application.service.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.request.AppointmentCreateDTO;
import odoonto.application.dto.response.AppointmentDTO;
import odoonto.application.mapper.AppointmentMapper;
import odoonto.application.port.in.appointment.AppointmentUpdateUseCase;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.aggregates.Appointment;
import odoonto.domain.repository.AppointmentRepository;
import odoonto.domain.repository.DoctorRepository;
import odoonto.domain.repository.PatientRepository;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para actualizar una cita
 */
@Service
public class AppointmentUpdateService implements AppointmentUpdateUseCase {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;

    @Autowired
    public AppointmentUpdateService(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public AppointmentDTO updateAppointment(String appointmentId, AppointmentCreateDTO appointmentCreateDTO) {
        // Validaciones básicas
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new DomainException("El ID de la cita no puede ser nulo o vacío");
        }
        
        if (appointmentCreateDTO == null) {
            throw new DomainException("Los datos de la cita no pueden ser nulos");
        }
        
        // Buscar la cita existente
        return appointmentRepository.findById(appointmentId)
            .switchIfEmpty(Mono.error(new DomainException("No existe una cita con el ID: " + appointmentId)))
            // Verificar que el doctor existe si se está cambiando
            .flatMap(existingAppointment -> {
                if (appointmentCreateDTO.getDoctorId() != null && 
                    !appointmentCreateDTO.getDoctorId().equals(existingAppointment.getDoctorId())) {
                    
                    return doctorRepository.findById(appointmentCreateDTO.getDoctorId())
                        .switchIfEmpty(Mono.error(new DomainException("No existe un doctor con el ID: " + appointmentCreateDTO.getDoctorId())))
                        .map(doctor -> existingAppointment);
                }
                return Mono.just(existingAppointment);
            })
            // Verificar que el paciente existe si se está cambiando
            .flatMap(existingAppointment -> {
                if (appointmentCreateDTO.getPatientId() != null && 
                    !appointmentCreateDTO.getPatientId().equals(existingAppointment.getPatientId())) {
                    
                    return patientRepository.findById(appointmentCreateDTO.getPatientId())
                        .switchIfEmpty(Mono.error(new DomainException("No existe un paciente con el ID: " + appointmentCreateDTO.getPatientId())))
                        .map(patient -> existingAppointment);
                }
                return Mono.just(existingAppointment);
            })
            // Actualizar la cita con los nuevos datos
            .flatMap(existingAppointment -> {
                // Actualizar los campos de la cita
                if (appointmentCreateDTO.getDoctorId() != null) {
                    existingAppointment.setDoctorId(appointmentCreateDTO.getDoctorId());
                }
                
                if (appointmentCreateDTO.getPatientId() != null) {
                    existingAppointment.setPatientId(appointmentCreateDTO.getPatientId());
                }
                
                if (appointmentCreateDTO.getDate() != null) {
                    existingAppointment.setDate(appointmentCreateDTO.getDate());
                }
                
                if (appointmentCreateDTO.getTime() != null) {
                    existingAppointment.setTime(appointmentCreateDTO.getTime());
                }
                
                if (appointmentCreateDTO.getDuration() != null) {
                    existingAppointment.setDuration(appointmentCreateDTO.getDuration());
                }
                
                if (appointmentCreateDTO.getNotes() != null) {
                    existingAppointment.setNotes(appointmentCreateDTO.getNotes());
                }
                
                // Guardar los cambios
                return appointmentRepository.save(existingAppointment);
            })
            .map(appointmentMapper::toDTO)
            .block(); // Bloquear para obtener el resultado
    }
} 