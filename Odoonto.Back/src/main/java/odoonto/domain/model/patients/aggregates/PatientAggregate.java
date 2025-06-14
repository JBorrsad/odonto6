package odoonto.domain.model.patients.aggregates;

import org.jmolecules.ddd.annotation.AggregateRoot;

import odoonto.domain.model.patients.valueobjects.PatientId;
import odoonto.domain.model.shared.valueobjects.PersonName;
import odoonto.domain.model.shared.valueobjects.EmailAddress;
import odoonto.domain.model.shared.valueobjects.PhoneNumber;
import odoonto.domain.model.shared.valueobjects.Address;
import odoonto.domain.model.shared.valueobjects.SexoValue;
import odoonto.domain.model.records.valueobjects.OdontogramId;
import odoonto.domain.model.records.valueobjects.MedicalRecordId;
import odoonto.domain.events.patients.PatientRegisteredEvent;
import odoonto.domain.exceptions.DomainException;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

@AggregateRoot
public class PatientAggregate {
    
    private final PatientId patientId;
    private PersonName name;
    private LocalDate birthDate;
    private SexoValue gender;
    private PhoneNumber phoneNumber;
    private EmailAddress emailAddress;
    private Address address;
    private final OdontogramId odontogramId;
    private final MedicalRecordId medicalRecordId;
    private final List<String> appointmentIds;
    private LocalDate lastVisitDate;

    public PatientAggregate(final PersonName name, 
                           final LocalDate birthDate,
                           final SexoValue gender, 
                           final PhoneNumber phoneNumber,
                           final EmailAddress emailAddress,
                           final Address address) {
        validateConstructorParameters(name, birthDate, gender, phoneNumber, emailAddress);
        
        this.patientId = PatientId.generate();
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.address = address;
        this.odontogramId = OdontogramId.fromPatientId(this.patientId);
        this.medicalRecordId = MedicalRecordId.fromPatientId(this.patientId);
        this.appointmentIds = new ArrayList<>();
    }

    private PatientAggregate(final PatientId patientId,
                            final PersonName name,
                            final LocalDate birthDate,
                            final SexoValue gender,
                            final PhoneNumber phoneNumber,
                            final EmailAddress emailAddress,
                            final Address address,
                            final List<String> appointmentIds,
                            final LocalDate lastVisitDate) {
        this.patientId = patientId;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.address = address;
        this.odontogramId = OdontogramId.fromPatientId(patientId);
        this.medicalRecordId = MedicalRecordId.fromPatientId(patientId);
        this.appointmentIds = new ArrayList<>(appointmentIds);
        this.lastVisitDate = lastVisitDate;
    }

    public static PatientAggregate reconstituteFromPersistence(
            final PatientId patientId,
            final PersonName name,
            final LocalDate birthDate,
            final SexoValue gender,
            final PhoneNumber phoneNumber,
            final EmailAddress emailAddress,
            final Address address,
            final List<String> appointmentIds,
            final LocalDate lastVisitDate) {
        
        validateReconstitutionParameters(patientId, name, birthDate, gender);
        
        return new PatientAggregate(patientId, name, birthDate, gender, 
                                   phoneNumber, emailAddress, address, 
                                   appointmentIds, lastVisitDate);
    }

    public void updateContactInfo(final PhoneNumber newPhoneNumber,
                                 final EmailAddress newEmailAddress,
                                 final Address newAddress) {
        if (newPhoneNumber != null) {
            this.phoneNumber = newPhoneNumber;
        }
        if (newEmailAddress != null) {
            this.emailAddress = newEmailAddress;
        }
        if (newAddress != null) {
            this.address = newAddress;
        }
    }

    public void updatePersonalData(final PersonName newName,
                                  final SexoValue newGender) {
        if (newName != null) {
            this.name = newName;
        }
        if (newGender != null) {
            this.gender = newGender;
        }
    }

    public void recordVisit() {
        this.lastVisitDate = LocalDate.now();
    }

    public void scheduleAppointment(final String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            throw new DomainException("Appointment ID cannot be null or empty");
        }
        if (!appointmentIds.contains(appointmentId)) {
            appointmentIds.add(appointmentId);
        }
    }

    public void cancelAppointment(final String appointmentId) {
        appointmentIds.remove(appointmentId);
    }

    public int calculateAge() {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public PatientRegisteredEvent generateRegistrationEvent() {
        return new PatientRegisteredEvent(patientId, name, emailAddress);
    }

    private static void validateConstructorParameters(final PersonName name,
                                                     final LocalDate birthDate,
                                                     final SexoValue gender,
                                                     final PhoneNumber phoneNumber,
                                                     final EmailAddress emailAddress) {
        if (name == null) {
            throw new DomainException("Patient name cannot be null");
        }
        if (birthDate == null) {
            throw new DomainException("Birth date cannot be null");
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new DomainException("Birth date cannot be in the future");
        }
        if (gender == null) {
            throw new DomainException("Gender cannot be null");
        }
        if (phoneNumber == null) {
            throw new DomainException("Phone number cannot be null");
        }
        if (emailAddress == null) {
            throw new DomainException("Email address cannot be null");
        }
    }

    private static void validateReconstitutionParameters(final PatientId patientId,
                                                        final PersonName name,
                                                        final LocalDate birthDate,
                                                        final SexoValue gender) {
        if (patientId == null) {
            throw new DomainException("Patient ID cannot be null");
        }
        validateConstructorParameters(name, birthDate, gender, 
                                    PhoneNumber.empty(), EmailAddress.empty());
    }

    public PatientId getPatientId() {
        return patientId;
    }

    public PersonName getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public SexoValue getGender() {
        return gender;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    public Address getAddress() {
        return address;
    }

    public OdontogramId getOdontogramId() {
        return odontogramId;
    }

    public MedicalRecordId getMedicalRecordId() {
        return medicalRecordId;
    }

    public List<String> getAppointmentIds() {
        return Collections.unmodifiableList(appointmentIds);
    }

    public LocalDate getLastVisitDate() {
        return lastVisitDate;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final PatientAggregate that = (PatientAggregate) other;
        return Objects.equals(patientId, that.patientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId);
    }
} 