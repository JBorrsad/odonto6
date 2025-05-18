package odoonto.application.service.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import odoonto.application.port.in.appointment.AppointmentCancelUseCase;
import odoonto.application.port.out.ReactiveAppointmentRepository;
import odoonto.domain.exceptions.DomainException;
import odoonto.domain.model.valueobjects.AppointmentStatus;
import reactor.core.publisher.Mono;

/**
 * Implementación del caso de uso para cancelar una cita
 */
@Service
public class AppointmentCancelService implements AppointmentCancelUseCase {

    private final ReactiveAppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentCancelService(ReactiveAppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Mono<Void> cancelAppointment(String appointmentId) {
        // Validaciones básicas
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Mono.error(new DomainException("El ID de la cita no puede ser nulo o vacío"));
        }
        
        // Buscar la cita existente y cancelarla
        return appointmentRepository.findById(appointmentId)
            .switchIfEmpty(Mono.error(new DomainException("No existe una cita con el ID: " + appointmentId)))
            .flatMap(appointment -> {
                // Verificar si la cita ya está cancelada
                if (appointment.getStatus() == AppointmentStatus.CANCELADA) {
                    return Mono.error(new DomainException("La cita ya está cancelada"));
                }
                
                // Verificar si la cita ya está completada
                if (appointment.getStatus() == AppointmentStatus.COMPLETADA) {
                    return Mono.error(new DomainException("No se puede cancelar una cita que ya está completada"));
                }
                
                // Cambiar el estado de la cita a cancelada
                appointment.setStatus(AppointmentStatus.CANCELADA);
                
                // Guardar los cambios
                return appointmentRepository.save(appointment);
            })
            .then();
    }
} 