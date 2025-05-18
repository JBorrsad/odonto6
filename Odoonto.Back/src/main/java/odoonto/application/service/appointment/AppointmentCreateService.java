package odoonto.application.service.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.request.AppointmentCreateDTO;
import odoonto.application.dto.response.AppointmentDTO;
import odoonto.application.mapper.AppointmentMapper;
import odoonto.application.port.in.appointment.AppointmentCreateUseCase;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.aggregates.Appointment;
import odoonto.domain.repository.AppointmentRepository;
import odoonto.domain.repository.DoctorRepository;
import odoonto.domain.repository.PatientRepository;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para crear una cita
 */
@Service
public class AppointmentCreateService implements AppointmentCreateUseCase {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;

    @Autowired
    public AppointmentCreateService(
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
    public AppointmentDTO createAppointment(AppointmentCreateDTO appointmentCreateDTO) {
        // Validaciones básicas
        if (appointmentCreateDTO == null) {
            throw new DomainException("Los datos de la cita no pueden ser nulos");
        }
        
        // Verificar que el doctor existe
        doctorRepository.findById(appointmentCreateDTO.getDoctorId())
            .orElseThrow(() -> new DomainException("No existe un doctor con el ID: " + appointmentCreateDTO.getDoctorId()));
            
        // Verificar que el paciente existe
        patientRepository.findById(appointmentCreateDTO.getPatientId())
            .orElseThrow(() -> new DomainException("No existe un paciente con el ID: " + appointmentCreateDTO.getPatientId()));
            
        // Convertir DTO a entidad de dominio
        Appointment appointment = appointmentMapper.toEntity(appointmentCreateDTO);
        
        // Guardar la cita
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Convertir entidad a DTO y devolver
        return appointmentMapper.toDTO(savedAppointment);
    }
} 