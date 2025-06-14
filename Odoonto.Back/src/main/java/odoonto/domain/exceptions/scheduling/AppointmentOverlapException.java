package odoonto.domain.exceptions.scheduling;

import odoonto.domain.exceptions.DomainException;

public final class AppointmentOverlapException extends DomainException {
    public AppointmentOverlapException(final String message) {
        super(message);
    }

    public AppointmentOverlapException(final String message, final Throwable cause) {
        super(message, cause);
    }
} 