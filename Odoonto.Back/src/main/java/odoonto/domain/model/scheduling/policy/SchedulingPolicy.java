package odoonto.domain.model.scheduling.policy;

import odoonto.domain.model.scheduling.valueobjects.AppointmentTime;
import odoonto.domain.model.records.valueobjects.TreatmentType;
import odoonto.domain.model.shared.valueobjects.DurationValue;

import java.time.LocalDateTime;
import java.time.LocalTime;

public final class SchedulingPolicy {

    private SchedulingPolicy() {}

    public static boolean isValidAppointmentTime(final AppointmentTime appointmentTime) {
        final LocalDateTime dateTime = appointmentTime.getValue();
        final LocalTime time = dateTime.toLocalTime();
        
        // Business hours: 8:00 AM to 6:00 PM
        return !time.isBefore(LocalTime.of(8, 0)) && !time.isAfter(LocalTime.of(18, 0));
    }

    public static boolean allowsEmergencyAppointment(final TreatmentType treatmentType) {
        return treatmentType == TreatmentType.EXTRACTION ||
               treatmentType == TreatmentType.ROOT_CANAL ||
               treatmentType == TreatmentType.CONSULTATION;
    }

    public static DurationValue getMinimumDurationFor(final TreatmentType treatmentType) {
        return switch (treatmentType) {
            case CONSULTATION -> new DurationValue(30);
            case CLEANING -> new DurationValue(60);
            case FILLING -> new DurationValue(45);
            case EXTRACTION -> new DurationValue(30);
            case ROOT_CANAL -> new DurationValue(90);
            case CROWN -> new DurationValue(120);
            case BRIDGE -> new DurationValue(150);
            case IMPLANT -> new DurationValue(180);
            case ORTHODONTICS -> new DurationValue(60);
            case PERIODONTAL -> new DurationValue(75);
        };
    }

    public static boolean requiresAdvanceBooking(final TreatmentType treatmentType) {
        return treatmentType == TreatmentType.CROWN ||
               treatmentType == TreatmentType.BRIDGE ||
               treatmentType == TreatmentType.IMPLANT ||
               treatmentType == TreatmentType.ORTHODONTICS;
    }

    public static int getMaximumDaysInAdvance() {
        return 90; // 3 months maximum
    }

    public static int getMinimumHoursNotice() {
        return 24; // 24 hours minimum notice for cancellation
    }
} 