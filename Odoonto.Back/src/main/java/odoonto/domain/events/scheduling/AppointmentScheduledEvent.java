package odoonto.domain.events.scheduling;

import odoonto.domain.events.shared.DomainEvent;
import odoonto.domain.model.shared.valueobjects.EventId;
import odoonto.domain.model.shared.valueobjects.TimestampValue;
import odoonto.domain.model.scheduling.valueobjects.AppointmentId;
import odoonto.domain.model.scheduling.valueobjects.AppointmentTime;
import odoonto.domain.model.patients.valueobjects.PatientId;
import odoonto.domain.model.staff.valueobjects.DoctorId;

public record AppointmentScheduledEvent(
    EventId id,
    TimestampValue occurredAt,
    AppointmentId appointmentId,
    PatientId patientId,
    DoctorId doctorId,
    AppointmentTime appointmentTime
) implements DomainEvent {

    @Override
    public EventId getEventId() {
        return id;
    }

    @Override
    public TimestampValue getOccurredAt() {
        return occurredAt;
    }

    public static AppointmentScheduledEvent create(final AppointmentId appointmentId,
                                                 final PatientId patientId,
                                                 final DoctorId doctorId,
                                                 final AppointmentTime appointmentTime) {
        return new AppointmentScheduledEvent(
            EventId.generate(),
            TimestampValue.now(),
            appointmentId,
            patientId,
            doctorId,
            appointmentTime
        );
    }
} 