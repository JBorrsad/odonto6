package odoonto.domain.model.patients.aggregates;

import org.jmolecules.ddd.annotation.AggregateRoot;
import odoonto.domain.model.patients.valueobjects.*;
import odoonto.domain.model.shared.valueobjects.MoneyValue;
import odoonto.domain.exceptions.patients.InvalidPatientDataException;
import java.util.Objects;

@AggregateRoot
public class PatientAggregate {
    private final PatientId patientId;
    private final EmailAddress email;
    private final PhoneNumber phoneNumber;
    private final AddressValue address;
    private final BirthDateValue birthDate;
    private final SexValue sex;
    private AllergyNotes allergyNotes;
    private MoneyValue balance;

    public PatientAggregate(final PatientId patientId, final EmailAddress email,
                           final PhoneNumber phoneNumber, final AddressValue address,
                           final BirthDateValue birthDate, final SexValue sex) {
        if (patientId == null) {
            throw new InvalidPatientDataException("PatientId cannot be null");
        }
        if (email == null) {
            throw new InvalidPatientDataException("Email cannot be null");
        }
        if (phoneNumber == null) {
            throw new InvalidPatientDataException("Phone number cannot be null");
        }
        if (address == null) {
            throw new InvalidPatientDataException("Address cannot be null");
        }
        if (birthDate == null) {
            throw new InvalidPatientDataException("Birth date cannot be null");
        }
        if (sex == null) {
            throw new InvalidPatientDataException("Sex cannot be null");
        }
        
        this.patientId = patientId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.birthDate = birthDate;
        this.sex = sex;
        this.allergyNotes = new AllergyNotes("");
        this.balance = new MoneyValue(java.math.BigDecimal.ZERO, "USD");
    }

    public void updateAllergyNotes(final AllergyNotes allergyNotes) {
        if (allergyNotes == null) {
            throw new InvalidPatientDataException("Allergy notes cannot be null");
        }
        this.allergyNotes = allergyNotes;
    }

    public void updateBalance(final MoneyValue newBalance) {
        if (newBalance == null) {
            throw new InvalidPatientDataException("Balance cannot be null");
        }
        this.balance = newBalance;
    }

    public PatientId getPatientId() {
        return patientId;
    }

    public EmailAddress getEmail() {
        return email;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public AddressValue getAddress() {
        return address;
    }

    public BirthDateValue getBirthDate() {
        return birthDate;
    }

    public SexValue getSex() {
        return sex;
    }

    public AllergyNotes getAllergyNotes() {
        return allergyNotes;
    }

    public MoneyValue getBalance() {
        return balance;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final PatientAggregate that = (PatientAggregate) obj;
        return Objects.equals(patientId, that.patientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId);
    }
} 