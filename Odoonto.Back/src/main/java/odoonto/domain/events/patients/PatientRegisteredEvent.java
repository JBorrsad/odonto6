package odoonto.domain.events.patients;

import odoonto.domain.events.shared.DomainEvent;
import odoonto.domain.model.shared.valueobjects.EventId;
import odoonto.domain.model.shared.valueobjects.TimestampValue;
import odoonto.domain.model.patients.valueobjects.PatientId;
import java.util.Objects;

public final class PatientRegisteredEvent implements DomainEvent {
    private final EventId eventId;
    private final TimestampValue occurredAt;
    private final PatientId patientId;

    public PatientRegisteredEvent(final EventId eventId, final TimestampValue occurredAt,
                                 final PatientId patientId) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("Occurred at cannot be null");
        }
        if (patientId == null) {
            throw new IllegalArgumentException("Patient ID cannot be null");
        }
        this.eventId = eventId;
        this.occurredAt = occurredAt;
        this.patientId = patientId;
    }

    public static PatientRegisteredEvent create(final PatientId patientId) {
        return new PatientRegisteredEvent(
            EventId.generate(),
            TimestampValue.now(),
            patientId
        );
    }

    @Override
    public EventId getEventId() {
        return eventId;
    }

    @Override
    public TimestampValue getOccurredAt() {
        return occurredAt;
    }

    public PatientId getPatientId() {
        return patientId;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final PatientRegisteredEvent that = (PatientRegisteredEvent) obj;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "PatientRegisteredEvent{eventId=" + eventId + 
               ", occurredAt=" + occurredAt + 
               ", patientId=" + patientId + '}';
    }
} 