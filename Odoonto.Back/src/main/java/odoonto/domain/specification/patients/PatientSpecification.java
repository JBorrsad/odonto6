package odoonto.domain.specification.patients;

import odoonto.domain.specification.shared.Specification;
import odoonto.domain.model.patients.aggregates.PatientAggregate;
import odoonto.domain.model.patients.valueobjects.BirthDateValue;

public final class PatientSpecification {

    private PatientSpecification() {}

    public static Specification<PatientAggregate> isMinor() {
        return patient -> patient.getBirthDate().isMinor();
    }

    public static Specification<PatientAggregate> isSenior() {
        return patient -> patient.getBirthDate().isSenior();
    }

    public static Specification<PatientAggregate> hasAllergies() {
        return patient -> patient.getAllergies().hasAllergies();
    }

    public static Specification<PatientAggregate> hasValidContactInfo() {
        return patient -> patient.getEmail() != null && patient.getPhone() != null;
    }

    public static Specification<PatientAggregate> isAdultWithoutAllergies() {
        return isMinor().not().and(hasAllergies().not());
    }
} 