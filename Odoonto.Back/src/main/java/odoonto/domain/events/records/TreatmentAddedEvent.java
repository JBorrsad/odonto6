package odoonto.domain.events.records;

import odoonto.domain.events.shared.DomainEvent;
import odoonto.domain.model.shared.valueobjects.EventId;
import odoonto.domain.model.shared.valueobjects.TimestampValue;
import odoonto.domain.model.records.valueobjects.MedicalRecordId;
import odoonto.domain.model.records.valueobjects.TreatmentId;

public record TreatmentAddedEvent(
    EventId id,
    TimestampValue occurredAt,
    MedicalRecordId medicalRecordId,
    TreatmentId treatmentId
) implements DomainEvent {

    @Override
    public EventId getEventId() {
        return id;
    }

    @Override
    public TimestampValue getOccurredAt() {
        return occurredAt;
    }

    public static TreatmentAddedEvent create(final MedicalRecordId medicalRecordId, 
                                           final TreatmentId treatmentId) {
        return new TreatmentAddedEvent(
            EventId.generate(),
            TimestampValue.now(),
            medicalRecordId,
            treatmentId
        );
    }
} 