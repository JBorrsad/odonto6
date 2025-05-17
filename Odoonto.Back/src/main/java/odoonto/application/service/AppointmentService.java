package odoonto.application.service;

import odoonto.domain.model.aggregates.Appointment;
import odoonto.application.dto.response.AppointmentDTO;
import odoonto.application.dto.request.AppointmentCreateDTO;
import odoonto.application.exceptions.PatientNotFoundException;
import odoonto.application.exceptions.DoctorNotFoundException;
import odoonto.application.exceptions.AppointmentConflictException;
import odoonto.application.mapper.AppointmentMapper;
import odoonto.domain.repository.AppointmentRepository;
import odoonto.domain.repository.DoctorRepository;
import odoonto.domain.repository.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Servicio de aplicación para gestionar citas
 */
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentMapper appointmentMapper;

    @Autowired
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentMapper = appointmentMapper;
    }

    /**
     * Obtiene todas las citas
     * @return Lista de DTOs de citas
     */
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .map(appointmentMapper::toDTO)
                .collectList()
                .block();
    }

    /**
     * Obtiene una cita por su ID
     * @param id ID de la cita
     * @return DTO de la cita
     * @throws RuntimeException si no se encuentra la cita
     */
    public AppointmentDTO getAppointmentById(String id) {
        return appointmentRepository.findById(id)
                .map(appointmentMapper::toDTO)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("No se encontró la cita con ID: " + id));
    }

    /**
     * Crea una nueva cita con validación de solapamientos
     * @param createDTO DTO con los datos de la cita
     * @return DTO de la cita creada
     * @throws PatientNotFoundException si no se encuentra el paciente
     * @throws DoctorNotFoundException si no se encuentra el doctor
     * @throws AppointmentConflictException si la cita solapa con otra existente
     */
    public AppointmentDTO createAppointment(AppointmentCreateDTO createDTO) {
        // Validar que existen paciente y doctor
        patientRepository.findById(createDTO.getPatientId())
                .blockOptional()
                .orElseThrow(() -> new PatientNotFoundException(createDTO.getPatientId()));
        
        doctorRepository.findById(createDTO.getDoctorId())
                .blockOptional()
                .orElseThrow(() -> new DoctorNotFoundException(createDTO.getDoctorId()));
        
        // Convertir a entidad de dominio
        Appointment appointment = appointmentMapper.toEntity(createDTO);
        
        // Verificar solapamientos
        LocalDateTime startTime = appointment.getStartAsLocalDateTime();
        LocalDateTime endTime = LocalDateTime.ofInstant(appointment.end(), ZoneId.systemDefault());
        
        // Buscar citas en el mismo rango de tiempo para el doctor
        String fromInstant = startTime.atZone(ZoneId.systemDefault()).toInstant().toString();
        String toInstant = endTime.atZone(ZoneId.systemDefault()).toInstant().toString();
        
        boolean hasConflict = appointmentRepository
                .findByDoctorIdAndStartGreaterThanEqualAndStartLessThanEqual(
                        appointment.getDoctorId(), fromInstant, toInstant)
                .hasElements()
                .block();
        
        if (hasConflict) {
            throw new AppointmentConflictException(
                    "Ya existe una cita para el doctor en ese horario");
        }
        
        // Guardar la cita
        Appointment savedAppointment = appointmentRepository.save(appointment).block();
        return appointmentMapper.toDTO(savedAppointment);
    }

    /**
     * Actualiza una cita existente
     * @param id ID de la cita a actualizar
     * @param updateDTO DTO con los datos actualizados
     * @return DTO de la cita actualizada
     * @throws RuntimeException si no se encuentra la cita
     */
    public AppointmentDTO updateAppointment(String id, AppointmentCreateDTO updateDTO) {
        return appointmentRepository.findById(id)
                .map(existingAppointment -> {
                    appointmentMapper.updateAppointmentFromDTO(updateDTO, existingAppointment);
                    return appointmentRepository.save(existingAppointment).block();
                })
                .map(appointmentMapper::toDTO)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("No se encontró la cita con ID: " + id));
    }

    /**
     * Elimina una cita
     * @param id ID de la cita a eliminar
     */
    public void deleteAppointment(String id) {
        appointmentRepository.deleteById(id).block();
    }

    /**
     * Obtiene las citas de un doctor en un rango de fechas
     * @param doctorId ID del doctor
     * @param from Fecha de inicio
     * @param to Fecha de fin
     * @return Lista de DTOs de citas
     */
    public List<AppointmentDTO> getAppointmentsByDoctorAndDateRange(
            String doctorId, String from, String to) {
        return appointmentRepository
                .findByDoctorIdAndStartGreaterThanEqualAndStartLessThanEqual(doctorId, from, to)
                .map(appointmentMapper::toDTO)
                .collectList()
                .block();
    }
} 