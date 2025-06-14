package odoonto.domain.events.patients;

import odoonto.domain.model.patients.valueobjects.PatientId;
import odoonto.domain.model.shared.valueobjects.EventId;
import odoonto.domain.model.shared.valueobjects.TimestampValue;

import java.util.Objects;

public final class PatientRegisteredEvent {
    
    private final EventId eventId;
    private final PatientId patientId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final TimestampValue occurredAt;

    private PatientRegisteredEvent(final EventId eventId,
                                  final PatientId patientId,
                                  final String firstName,
                                  final String lastName,
                                  final String email,
                                  final String phone,
                                  final TimestampValue occurredAt) {
        validateParameters(eventId, patientId, firstName, lastName, occurredAt);
        this.eventId = eventId;
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.occurredAt = occurredAt;
    }

    public static PatientRegisteredEvent create(final PatientId patientId,
                                               final String firstName,
                                               final String lastName,
                                               final String email,
                                               final String phone) {
        return new PatientRegisteredEvent(
            EventId.generate(),
            patientId,
            firstName,
            lastName,
            email,
            phone,
            TimestampValue.now()
        );
    }

    public static PatientRegisteredEvent reconstruct(final EventId eventId,
                                                    final PatientId patientId,
                                                    final String firstName,
                                                    final String lastName,
                                                    final String email,
                                                    final String phone,
                                                    final TimestampValue occurredAt) {
        return new PatientRegisteredEvent(eventId, patientId, firstName, lastName, 
                                         email, phone, occurredAt);
    }

    private static void validateParameters(final EventId eventId,
                                          final PatientId patientId,
                                          final String firstName,
                                          final String lastName,
                                          final TimestampValue occurredAt) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        if (patientId == null) {
            throw new IllegalArgumentException("Patient ID cannot be null");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("Occurred timestamp cannot be null");
        }
    }

    public EventId getEventId() {
        return eventId;
    }

    public PatientId getPatientId() {
        return patientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public TimestampValue getOccurredAt() {
        return occurredAt;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final PatientRegisteredEvent that = (PatientRegisteredEvent) other;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "PatientRegisteredEvent{" +
                "eventId=" + eventId +
                ", patientId=" + patientId +
                ", name='" + firstName + " " + lastName + '\'' +
                ", occurredAt=" + occurredAt +
                '}';
    }
} 