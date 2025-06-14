package odoonto.domain.events.scheduling;

import odoonto.domain.events.shared.DomainEvent;
import odoonto.domain.model.shared.valueobjects.EventId;
import odoonto.domain.model.shared.valueobjects.TimestampValue;
import odoonto.domain.model.scheduling.valueobjects.AppointmentId;
import odoonto.domain.model.scheduling.valueobjects.AppointmentStatus;

public record AppointmentStatusChangedEvent(
    EventId id,
    TimestampValue occurredAt,
    AppointmentId appointmentId,
    AppointmentStatus oldStatus,
    AppointmentStatus newStatus
) implements DomainEvent {

    @Override
    public EventId getEventId() {
        return id;
    }

    @Override
    public TimestampValue getOccurredAt() {
        return occurredAt;
    }

    public static AppointmentStatusChangedEvent create(final AppointmentId appointmentId,
                                                      final AppointmentStatus oldStatus,
                                                      final AppointmentStatus newStatus) {
        return new AppointmentStatusChangedEvent(
            EventId.generate(),
            TimestampValue.now(),
            appointmentId,
            oldStatus,
            newStatus
        );
    }
} 