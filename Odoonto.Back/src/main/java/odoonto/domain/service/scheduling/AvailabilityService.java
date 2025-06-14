package odoonto.domain.service.scheduling;

import org.jmolecules.ddd.annotation.Service;

import odoonto.domain.model.scheduling.entities.AvailabilityCalendarEntity;
import odoonto.domain.model.scheduling.valueobjects.AppointmentTime;
import odoonto.domain.model.staff.valueobjects.DoctorId;
import odoonto.domain.model.shared.valueobjects.DurationValue;
import odoonto.domain.repository.scheduling.AppointmentRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class AvailabilityService {
    private final AppointmentRepository appointmentRepository;

    public AvailabilityService(final AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public boolean isSlotAvailable(final DoctorId doctorId,
                                  final AppointmentTime appointmentTime,
                                  final DurationValue duration) {
        final List<odoonto.domain.model.scheduling.aggregates.AppointmentAggregate> existingAppointments = 
            appointmentRepository.findByDoctorIdAndDate(doctorId, appointmentTime.getDate());

        return existingAppointments.stream()
            .noneMatch(appointment -> 
                appointment.getAppointmentTime().overlapsWith(appointmentTime, duration));
    }

    public List<AppointmentTime> getAvailableSlots(final DoctorId doctorId,
                                                  final LocalDate date,
                                                  final DurationValue appointmentDuration) {
        // This would typically involve complex scheduling logic
        // For now, returning empty list as placeholder
        return List.of();
    }

    public boolean canRescheduleAppointment(final DoctorId doctorId,
                                          final AppointmentTime currentTime,
                                          final AppointmentTime newTime,
                                          final DurationValue duration) {
        // Check if new time is available (excluding current appointment)
        final List<odoonto.domain.model.scheduling.aggregates.AppointmentAggregate> existingAppointments = 
            appointmentRepository.findByDoctorIdAndDate(doctorId, newTime.getDate());

        return existingAppointments.stream()
            .filter(appointment -> !appointment.getAppointmentTime().equals(currentTime))
            .noneMatch(appointment -> 
                appointment.getAppointmentTime().overlapsWith(newTime, duration));
    }
} 