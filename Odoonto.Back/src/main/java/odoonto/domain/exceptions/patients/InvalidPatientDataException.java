package odoonto.domain.exceptions.patients;

import odoonto.domain.exceptions.DomainException;

public final class InvalidPatientDataException extends DomainException {
    public InvalidPatientDataException(final String message) {
        super(message);
    }
} 