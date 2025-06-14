package odoonto.domain.exceptions.records;

import odoonto.domain.exceptions.DomainException;

public final class InvalidTreatmentException extends DomainException {
    public InvalidTreatmentException(final String message) {
        super(message);
    }

    public InvalidTreatmentException(final String message, final Throwable cause) {
        super(message, cause);
    }
} 