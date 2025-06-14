package odoonto.domain.exceptions.patients;

import odoonto.domain.exceptions.DomainException;

public final class InvalidEmailException extends DomainException {
    public InvalidEmailException(final String message) {
        super(message);
    }
} 