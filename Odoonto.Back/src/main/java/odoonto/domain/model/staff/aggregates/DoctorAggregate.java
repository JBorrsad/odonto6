package odoonto.domain.model.staff.aggregates;

import org.jmolecules.ddd.annotation.AggregateRoot;

import odoonto.domain.events.shared.DomainEvents;
import odoonto.domain.events.staff.DoctorCreatedEvent;
import odoonto.domain.model.staff.valueobjects.DoctorId;
import odoonto.domain.model.staff.valueobjects.SpecialtyValue;
import odoonto.domain.model.staff.valueobjects.ScheduleValue;
import odoonto.domain.model.patients.valueobjects.EmailAddress;
import odoonto.domain.model.patients.valueobjects.PhoneNumber;

@AggregateRoot
public class DoctorAggregate {
    private final DoctorId doctorId;
    private final String fullName;
    private final EmailAddress email;
    private final PhoneNumber phone;
    private final SpecialtyValue specialty;
    private final ScheduleValue schedule;

    public DoctorAggregate(final DoctorId doctorId,
                          final String fullName,
                          final EmailAddress email,
                          final PhoneNumber phone,
                          final SpecialtyValue specialty,
                          final ScheduleValue schedule) {
        this.doctorId = doctorId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.specialty = specialty;
        this.schedule = schedule;
    }

    public void register() {
        DomainEvents.raise(DoctorCreatedEvent.create(
            this.doctorId,
            this.fullName,
            this.specialty
        ));
    }

    public DoctorId getDoctorId() {
        return doctorId;
    }

    public String getFullName() {
        return fullName;
    }

    public EmailAddress getEmail() {
        return email;
    }

    public PhoneNumber getPhone() {
        return phone;
    }

    public SpecialtyValue getSpecialty() {
        return specialty;
    }

    public ScheduleValue getSchedule() {
        return schedule;
    }
} 