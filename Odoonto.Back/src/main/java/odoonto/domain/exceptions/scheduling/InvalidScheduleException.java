package odoonto.domain.exceptions.scheduling;

import odoonto.domain.exceptions.DomainException;

public final class InvalidScheduleException extends DomainException {

    public InvalidScheduleException(final String message) {
        super(message);
    }

    public InvalidScheduleException(final String message, final Throwable cause) {
        super(message, cause);
    }
} 