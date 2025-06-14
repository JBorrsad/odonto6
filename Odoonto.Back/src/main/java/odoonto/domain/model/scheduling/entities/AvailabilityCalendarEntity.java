package odoonto.domain.model.scheduling.entities;

import org.jmolecules.ddd.annotation.Entity;

import odoonto.domain.model.scheduling.valueobjects.TimeSlotValue;
import odoonto.domain.model.scheduling.valueobjects.AppointmentTime;
import odoonto.domain.model.staff.valueobjects.DoctorId;
import odoonto.domain.model.shared.valueobjects.DurationValue;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
public class AvailabilityCalendarEntity {
    private final DoctorId doctorId;
    private final LocalDate date;
    private final List<TimeSlotValue> availableSlots;
    private final List<AppointmentTime> bookedSlots;

    public AvailabilityCalendarEntity(final DoctorId doctorId,
                                    final LocalDate date,
                                    final List<TimeSlotValue> availableSlots,
                                    final List<AppointmentTime> bookedSlots) {
        this.doctorId = doctorId;
        this.date = date;
        this.availableSlots = new ArrayList<>(availableSlots);
        this.bookedSlots = new ArrayList<>(bookedSlots);
    }

    public boolean isSlotAvailable(final AppointmentTime appointmentTime, 
                                  final DurationValue duration) {
        if (!appointmentTime.getDate().equals(this.date)) {
            return false;
        }
        
        // Check if slot conflicts with any booked appointments
        return bookedSlots.stream()
            .noneMatch(bookedSlot -> appointmentTime.overlapsWith(bookedSlot, duration));
    }

    public void bookSlot(final AppointmentTime appointmentTime) {
        if (appointmentTime.getDate().equals(this.date)) {
            this.bookedSlots.add(appointmentTime);
        }
    }

    public void cancelSlot(final AppointmentTime appointmentTime) {
        this.bookedSlots.removeIf(slot -> slot.equals(appointmentTime));
    }

    public DoctorId getDoctorId() {
        return doctorId;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<TimeSlotValue> getAvailableSlots() {
        return new ArrayList<>(availableSlots);
    }

    public List<AppointmentTime> getBookedSlots() {
        return new ArrayList<>(bookedSlots);
    }
} 