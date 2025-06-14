package odoonto.domain.model.scheduling.entities;

import org.jmolecules.ddd.annotation.Entity;

import odoonto.domain.model.staff.valueobjects.DoctorId;
import odoonto.domain.model.scheduling.valueobjects.AppointmentTime;
import odoonto.domain.exceptions.DomainException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Entity
public class AvailabilityCalendarEntity {
    
    private final DoctorId doctorId;
    private final LocalDate date;
    private final Map<LocalTime, SlotStatus> timeSlots;

    private AvailabilityCalendarEntity(final DoctorId doctorId,
                                      final LocalDate date,
                                      final Map<LocalTime, SlotStatus> timeSlots) {
        this.doctorId = doctorId;
        this.date = date;
        this.timeSlots = new HashMap<>(timeSlots);
    }

    public static AvailabilityCalendarEntity createForDay(final DoctorId doctorId,
                                                          final LocalDate date) {
        validateCreationParameters(doctorId, date);
        
        final Map<LocalTime, SlotStatus> defaultSlots = generateDefaultSlots();
        return new AvailabilityCalendarEntity(doctorId, date, defaultSlots);
    }

    public void markSlotAsOccupied(final LocalTime time) {
        validateTimeSlot(time);
        
        if (!timeSlots.containsKey(time)) {
            throw new DomainException("Time slot does not exist: " + time);
        }
        
        timeSlots.put(time, SlotStatus.OCCUPIED);
    }

    public void markSlotAsAvailable(final LocalTime time) {
        validateTimeSlot(time);
        
        if (!timeSlots.containsKey(time)) {
            throw new DomainException("Time slot does not exist: " + time);
        }
        
        timeSlots.put(time, SlotStatus.AVAILABLE);
    }

    public boolean isSlotAvailable(final LocalTime time) {
        validateTimeSlot(time);
        return timeSlots.get(time) == SlotStatus.AVAILABLE;
    }

    public boolean isAvailableAt(final AppointmentTime appointmentTime) {
        if (appointmentTime == null) {
            throw new DomainException("Appointment time cannot be null");
        }
        
        if (!appointmentTime.getDate().equals(this.date)) {
            return false;
        }
        
        return isSlotAvailable(appointmentTime.getTime());
    }

    public List<LocalTime> getAvailableSlots() {
        return timeSlots.entrySet().stream()
                .filter(entry -> entry.getValue() == SlotStatus.AVAILABLE)
                .map(Map.Entry::getKey)
                .sorted()
                .toList();
    }

    private static Map<LocalTime, SlotStatus> generateDefaultSlots() {
        final Map<LocalTime, SlotStatus> slots = new HashMap<>();
        
        for (int hour = 8; hour < 18; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                final LocalTime time = LocalTime.of(hour, minute);
                slots.put(time, SlotStatus.AVAILABLE);
            }
        }
        
        return slots;
    }

    private static void validateCreationParameters(final DoctorId doctorId,
                                                  final LocalDate date) {
        if (doctorId == null) {
            throw new DomainException("Doctor ID cannot be null");
        }
        if (date == null) {
            throw new DomainException("Date cannot be null");
        }
    }

    private static void validateTimeSlot(final LocalTime time) {
        if (time == null) {
            throw new DomainException("Time cannot be null");
        }
    }

    public DoctorId getDoctorId() {
        return doctorId;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final AvailabilityCalendarEntity that = (AvailabilityCalendarEntity) other;
        return Objects.equals(doctorId, that.doctorId) && 
               Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId, date);
    }

    public enum SlotStatus {
        AVAILABLE,
        OCCUPIED,
        BLOCKED
    }
} 