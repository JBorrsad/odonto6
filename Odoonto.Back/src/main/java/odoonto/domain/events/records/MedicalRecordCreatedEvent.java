package odoonto.domain.events.records;

import odoonto.domain.events.shared.DomainEvent;
import odoonto.domain.model.shared.valueobjects.EventId;
import odoonto.domain.model.shared.valueobjects.TimestampValue;
import odoonto.domain.model.records.valueobjects.MedicalRecordId;
import odoonto.domain.model.patients.valueobjects.PatientId;

public record MedicalRecordCreatedEvent(
    EventId id,
    TimestampValue occurredAt,
    MedicalRecordId medicalRecordId,
    PatientId patientId
) implements DomainEvent {

    @Override
    public EventId getEventId() {
        return id;
    }

    @Override
    public TimestampValue getOccurredAt() {
        return occurredAt;
    }

    public static MedicalRecordCreatedEvent create(final MedicalRecordId medicalRecordId, 
                                                  final PatientId patientId) {
        return new MedicalRecordCreatedEvent(
            EventId.generate(),
            TimestampValue.now(),
            medicalRecordId,
            patientId
        );
    }
} 