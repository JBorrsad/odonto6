package odoonto.application.service.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.port.in.appointment.AppointmentCancelUseCase;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.aggregates.Appointment;
import odoonto.domain.model.valueobjects.AppointmentStatus;
import odoonto.domain.repository.AppointmentRepository;


/**
 * Implementación del caso de uso para cancelar una cita
 */
@Service
public class AppointmentCancelService implements AppointmentCancelUseCase {

    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentCancelService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public void cancelAppointment(String appointmentId) {
        // Validaciones básicas
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new DomainException("El ID de la cita no puede ser nulo o vacío");
        }
        
        // Buscar la cita existente y cancelarla
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new DomainException("No existe una cita con el ID: " + appointmentId));
        
        // Verificar si la cita ya está cancelada
        if (appointment.getStatus() == AppointmentStatus.CANCELADA) {
            throw new DomainException("La cita ya está cancelada");
        }
        
        // Verificar si la cita ya está completada
        if (appointment.getStatus() == AppointmentStatus.COMPLETADA) {
            throw new DomainException("No se puede cancelar una cita que ya está completada");
        }
        
        // Cambiar el estado de la cita a cancelada
        appointment.setStatus(AppointmentStatus.CANCELADA);
        
        // Guardar los cambios
        appointmentRepository.save(appointment);
    }
} 