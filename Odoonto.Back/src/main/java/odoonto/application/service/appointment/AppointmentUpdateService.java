package odoonto.application.service.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.dto.request.AppointmentCreateDTO;
import odoonto.application.dto.response.AppointmentDTO;
import odoonto.application.mapper.AppointmentMapper;
import odoonto.application.port.in.appointment.AppointmentUpdateUseCase;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.aggregates.Appointment;
import odoonto.domain.model.aggregates.Doctor;
import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.repository.AppointmentRepository;
import odoonto.domain.repository.DoctorRepository;
import odoonto.domain.repository.PatientRepository;

import java.util.Optional;

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
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            throw new DomainException("No existe una cita con el ID: " + appointmentId);
        }
        
        Appointment existingAppointment = appointmentOpt.get();
        
        // Verificar que el doctor existe si se está cambiando
        if (appointmentCreateDTO.getDoctorId() != null && 
            !appointmentCreateDTO.getDoctorId().equals(existingAppointment.getDoctorId())) {
            
            Optional<Doctor> doctorOpt = doctorRepository.findById(appointmentCreateDTO.getDoctorId());
            if (doctorOpt.isEmpty()) {
                throw new DomainException("No existe un doctor con el ID: " + appointmentCreateDTO.getDoctorId());
            }
        }
        
        // Verificar que el paciente existe si se está cambiando
        if (appointmentCreateDTO.getPatientId() != null && 
            !appointmentCreateDTO.getPatientId().equals(existingAppointment.getPatientId())) {
            
            Optional<Patient> patientOpt = patientRepository.findById(appointmentCreateDTO.getPatientId());
            if (patientOpt.isEmpty()) {
                throw new DomainException("No existe un paciente con el ID: " + appointmentCreateDTO.getPatientId());
            }
        }
        
        // Actualizar los campos de la cita usando el mapper
        appointmentMapper.updateAppointmentFromDTO(appointmentCreateDTO, existingAppointment);
        
        // Guardar los cambios
        Appointment updatedAppointment = appointmentRepository.save(existingAppointment);
        return appointmentMapper.toDTO(updatedAppointment);
    }
} 