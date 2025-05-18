package odoonto.application.service;

import odoonto.domain.model.aggregates.Appointment;
import odoonto.domain.model.aggregates.Patient;
import odoonto.domain.model.aggregates.Doctor;
import odoonto.application.dto.response.AppointmentDTO;
import odoonto.application.dto.request.AppointmentCreateDTO;
import odoonto.application.exceptions.PatientNotFoundException;
import odoonto.application.exceptions.DoctorNotFoundException;
import odoonto.application.exceptions.AppointmentConflictException;
import odoonto.application.mapper.AppointmentMapper;
import odoonto.application.port.out.ReactiveAppointmentRepository;
import odoonto.application.port.out.ReactivePatientRepository;
import odoonto.application.port.out.ReactiveDoctorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Servicio de aplicación para gestionar citas
 * Implementación completamente reactiva
 */
@Service
public class AppointmentService {

    private final ReactiveAppointmentRepository appointmentRepository;
    private final ReactivePatientRepository patientRepository;
    private final ReactiveDoctorRepository doctorRepository;
    private final AppointmentMapper appointmentMapper;

    @Autowired
    public AppointmentService(
            ReactiveAppointmentRepository appointmentRepository,
            ReactivePatientRepository patientRepository,
            ReactiveDoctorRepository doctorRepository,
            AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentMapper = appointmentMapper;
    }

    /**
     * Obtiene todas las citas
     * @return Flux de DTOs de citas
     */
    public Flux<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .map(appointmentMapper::toDTO);
    }

    /**
     * Obtiene una cita por su ID
     * @param id ID de la cita
     * @return Mono con el DTO de la cita
     * @throws RuntimeException si no se encuentra la cita
     */
    public Mono<AppointmentDTO> getAppointmentById(String id) {
        return appointmentRepository.findById(id)
                .map(appointmentMapper::toDTO)
                .switchIfEmpty(Mono.error(new RuntimeException("No se encontró la cita con ID: " + id)));
    }

    /**
     * Crea una nueva cita con validación de solapamientos
     * @param createDTO DTO con los datos de la cita
     * @return Mono con el DTO de la cita creada
     * @throws PatientNotFoundException si no se encuentra el paciente
     * @throws DoctorNotFoundException si no se encuentra el doctor
     * @throws AppointmentConflictException si la cita solapa con otra existente
     */
    public Mono<AppointmentDTO> createAppointment(AppointmentCreateDTO createDTO) {
        // Validar que existen paciente y doctor
        Mono<Patient> patientMono = patientRepository.findById(createDTO.getPatientId())
                .switchIfEmpty(Mono.error(new PatientNotFoundException(createDTO.getPatientId())));
        
        Mono<Doctor> doctorMono = doctorRepository.findById(createDTO.getDoctorId())
                .switchIfEmpty(Mono.error(new DoctorNotFoundException(createDTO.getDoctorId())));
        
        // Esperar a que ambos existan
        return Mono.zip(patientMono, doctorMono)
                .flatMap(tuple -> {
                    // Convertir a entidad de dominio
                    Appointment appointment = appointmentMapper.toEntity(createDTO);
                    
                    // Verificar solapamientos
                    LocalDateTime startTime = appointment.getStartAsLocalDateTime();
                    LocalDateTime endTime = startTime.plusMinutes(appointment.getDuration());
                    
                    // Buscar citas en el mismo rango de tiempo para el doctor
                    String fromInstant = startTime.atZone(ZoneId.systemDefault()).toInstant().toString();
                    String toInstant = endTime.atZone(ZoneId.systemDefault()).toInstant().toString();
                    
                    return appointmentRepository.findByDoctorIdAndDateRange(
                            appointment.getDoctorId(), fromInstant, toInstant)
                            .collectList()
                            .flatMap(conflictingAppointments -> {
                                if (!conflictingAppointments.isEmpty()) {
                                    return Mono.error(new AppointmentConflictException(
                                            "Ya existe una cita para el doctor en ese horario"));
                                }
                                
                                // Guardar la cita
                                return appointmentRepository.save(appointment);
                            });
                })
                .map(appointmentMapper::toDTO);
    }

    /**
     * Actualiza una cita existente
     * @param id ID de la cita a actualizar
     * @param updateDTO DTO con los datos actualizados
     * @return Mono con el DTO de la cita actualizada
     * @throws RuntimeException si no se encuentra la cita
     */
    public Mono<AppointmentDTO> updateAppointment(String id, AppointmentCreateDTO updateDTO) {
        return appointmentRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("No se encontró la cita con ID: " + id)))
                .flatMap(existingAppointment -> {
                    appointmentMapper.updateAppointmentFromDTO(updateDTO, existingAppointment);
                    return appointmentRepository.save(existingAppointment);
                })
                .map(appointmentMapper::toDTO);
    }

    /**
     * Elimina una cita
     * @param id ID de la cita a eliminar
     * @return Mono que completa cuando se elimina la cita
     */
    public Mono<Void> deleteAppointment(String id) {
        return appointmentRepository.deleteById(id);
    }

    /**
     * Obtiene las citas de un doctor en un rango de fechas
     * @param doctorId ID del doctor
     * @param from Fecha de inicio
     * @param to Fecha de fin
     * @return Flux de DTOs de citas
     */
    public Flux<AppointmentDTO> getAppointmentsByDoctorAndDateRange(
            String doctorId, String from, String to) {
        return appointmentRepository
                .findByDoctorIdAndDateRange(doctorId, from, to)
                .map(appointmentMapper::toDTO);
    }
} 