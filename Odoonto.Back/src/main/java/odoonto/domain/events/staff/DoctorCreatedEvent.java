package odoonto.domain.events.staff;

import odoonto.domain.events.shared.DomainEvent;
import odoonto.domain.model.shared.valueobjects.EventId;
import odoonto.domain.model.shared.valueobjects.TimestampValue;
import odoonto.domain.model.staff.valueobjects.DoctorId;
import odoonto.domain.model.staff.valueobjects.SpecialtyValue;

public record DoctorCreatedEvent(
    EventId id,
    TimestampValue occurredAt,
    DoctorId doctorId,
    String fullName,
    SpecialtyValue specialty
) implements DomainEvent {

    @Override
    public EventId getEventId() {
        return id;
    }

    @Override
    public TimestampValue getOccurredAt() {
        return occurredAt;
    }

    public static DoctorCreatedEvent create(final DoctorId doctorId,
                                          final String fullName,
                                          final SpecialtyValue specialty) {
        return new DoctorCreatedEvent(
            EventId.generate(),
            TimestampValue.now(),
            doctorId,
            fullName,
            specialty
        );
    }
} 