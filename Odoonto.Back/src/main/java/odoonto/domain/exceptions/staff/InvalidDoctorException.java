package odoonto.domain.exceptions.staff;

import odoonto.domain.exceptions.DomainException;

public final class InvalidDoctorException extends DomainException {

    public InvalidDoctorException(final String message) {
        super(message);
    }

    public InvalidDoctorException(final String message, final Throwable cause) {
        super(message, cause);
    }
} 