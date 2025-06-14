package odoonto.domain.model.staff.aggregates;

import org.jmolecules.ddd.annotation.AggregateRoot;

import odoonto.domain.model.staff.valueobjects.DoctorId;
import odoonto.domain.model.staff.valueobjects.ScheduleValue;
import odoonto.domain.model.staff.valueobjects.StaffRole;
import odoonto.domain.model.shared.valueobjects.PersonName;
import odoonto.domain.model.shared.valueobjects.EmailAddress;
import odoonto.domain.model.shared.valueobjects.PhoneNumber;
import odoonto.domain.model.scheduling.valueobjects.AppointmentTime;
import odoonto.domain.exceptions.DomainException;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

@AggregateRoot
public class DoctorAggregate {
    
    private final DoctorId doctorId;
    private PersonName name;
    private EmailAddress emailAddress;
    private PhoneNumber phoneNumber;
    private final StaffRole role;
    private ScheduleValue schedule;
    private final List<String> specialties;
    private boolean isActive;

    public DoctorAggregate(final PersonName name,
                          final EmailAddress emailAddress,
                          final PhoneNumber phoneNumber,
                          final List<String> specialties) {
        validateConstructorParameters(name, emailAddress, phoneNumber);
        
        this.doctorId = DoctorId.generate();
        this.name = name;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.role = StaffRole.DENTIST;
        this.schedule = ScheduleValue.defaultSchedule();
        this.specialties = new ArrayList<>(specialties != null ? specialties : new ArrayList<>());
        this.isActive = true;
    }

    private DoctorAggregate(final DoctorId doctorId,
                           final PersonName name,
                           final EmailAddress emailAddress,
                           final PhoneNumber phoneNumber,
                           final ScheduleValue schedule,
                           final List<String> specialties,
                           final boolean isActive) {
        this.doctorId = doctorId;
        this.name = name;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.role = StaffRole.DENTIST;
        this.schedule = schedule;
        this.specialties = new ArrayList<>(specialties);
        this.isActive = isActive;
    }

    public static DoctorAggregate reconstituteFromPersistence(
            final DoctorId doctorId,
            final PersonName name,
            final EmailAddress emailAddress,
            final PhoneNumber phoneNumber,
            final ScheduleValue schedule,
            final List<String> specialties,
            final boolean isActive) {
        
        validateReconstitutionParameters(doctorId, name, emailAddress, phoneNumber, schedule);
        
        return new DoctorAggregate(doctorId, name, emailAddress, phoneNumber, 
                                  schedule, specialties, isActive);
    }

    public void updateSchedule(final ScheduleValue newSchedule) {
        if (newSchedule == null) {
            throw new DomainException("New schedule cannot be null");
        }
        this.schedule = newSchedule;
    }

    public void updateContactInfo(final EmailAddress newEmailAddress,
                                 final PhoneNumber newPhoneNumber) {
        if (newEmailAddress != null) {
            this.emailAddress = newEmailAddress;
        }
        if (newPhoneNumber != null) {
            this.phoneNumber = newPhoneNumber;
        }
    }

    public void updatePersonalInfo(final PersonName newName) {
        if (newName != null) {
            this.name = newName;
        }
    }

    public void addSpecialty(final String specialty) {
        if (specialty == null || specialty.trim().isEmpty()) {
            throw new DomainException("Specialty cannot be null or empty");
        }
        if (!specialties.contains(specialty)) {
            specialties.add(specialty);
        }
    }

    public void removeSpecialty(final String specialty) {
        specialties.remove(specialty);
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean isAvailable(final AppointmentTime slot) {
        if (slot == null) {
            throw new DomainException("Appointment slot cannot be null");
        }
        
        if (!isActive) {
            return false;
        }
        
        return schedule.isAvailableAt(slot);
    }

    public boolean hasSpecialty(final String specialty) {
        return specialties.contains(specialty);
    }

    public boolean canTreatSpecialty(final String requiredSpecialty) {
        return specialties.isEmpty() || hasSpecialty(requiredSpecialty);
    }

    private static void validateConstructorParameters(final PersonName name,
                                                     final EmailAddress emailAddress,
                                                     final PhoneNumber phoneNumber) {
        if (name == null) {
            throw new DomainException("Doctor name cannot be null");
        }
        if (emailAddress == null) {
            throw new DomainException("Doctor email cannot be null");
        }
        if (phoneNumber == null) {
            throw new DomainException("Doctor phone number cannot be null");
        }
    }

    private static void validateReconstitutionParameters(final DoctorId doctorId,
                                                        final PersonName name,
                                                        final EmailAddress emailAddress,
                                                        final PhoneNumber phoneNumber,
                                                        final ScheduleValue schedule) {
        if (doctorId == null) {
            throw new DomainException("Doctor ID cannot be null");
        }
        if (schedule == null) {
            throw new DomainException("Doctor schedule cannot be null");
        }
        validateConstructorParameters(name, emailAddress, phoneNumber);
    }

    public DoctorId getDoctorId() {
        return doctorId;
    }

    public PersonName getName() {
        return name;
    }

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public StaffRole getRole() {
        return role;
    }

    public ScheduleValue getSchedule() {
        return schedule;
    }

    public List<String> getSpecialties() {
        return new ArrayList<>(specialties);
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final DoctorAggregate that = (DoctorAggregate) other;
        return Objects.equals(doctorId, that.doctorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId);
    }
} 