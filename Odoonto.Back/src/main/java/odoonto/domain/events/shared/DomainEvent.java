package odoonto.domain.events.shared;

import odoonto.domain.model.shared.valueobjects.EventId;
import odoonto.domain.model.shared.valueobjects.TimestampValue;

public interface DomainEvent {
    EventId getEventId();
    TimestampValue getOccurredAt();
} 