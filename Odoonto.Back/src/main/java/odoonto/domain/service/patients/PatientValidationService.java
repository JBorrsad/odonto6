package odoonto.domain.service.patients;

import odoonto.domain.exceptions.DomainException;
import java.time.LocalDate;

public final class PatientValidationService {

    public void validateBasicData(final String name, final LocalDate birthDate) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException("Name cannot be null or empty");
        }
        
        if (birthDate == null) {
            throw new DomainException("Birth date cannot be null");
        }
        
        if (birthDate.isAfter(LocalDate.now())) {
            throw new DomainException("Birth date cannot be in the future");
        }
    }
} 