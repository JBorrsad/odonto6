package odoonto.domain.events.scheduling;

import odoonto.domain.events.shared.DomainEvent;
import odoonto.domain.model.shared.valueobjects.EventId;
import odoonto.domain.model.shared.valueobjects.TimestampValue;
import odoonto.domain.model.scheduling.valueobjects.AppointmentId;
import odoonto.domain.model.records.valueobjects.TreatmentId;
import odoonto.domain.model.patients.valueobjects.PatientId;
import odoonto.domain.model.staff.valueobjects.DoctorId;

public record TreatmentCompletedEvent(
    EventId id,
    TimestampValue occurredAt,
    AppointmentId appointmentId,
    TreatmentId treatmentId,
    PatientId patientId,
    DoctorId doctorId
) implements DomainEvent {

    @Override
    public EventId getEventId() {
        return id;
    }

    @Override
    public TimestampValue getOccurredAt() {
        return occurredAt;
    }

    public static TreatmentCompletedEvent create(final AppointmentId appointmentId,
                                               final TreatmentId treatmentId,
                                               final PatientId patientId,
                                               final DoctorId doctorId) {
        return new TreatmentCompletedEvent(
            EventId.generate(),
            TimestampValue.now(),
            appointmentId,
            treatmentId,
            patientId,
            doctorId
        );
    }
} 