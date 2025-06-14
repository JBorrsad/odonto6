package odoonto.domain.exceptions.records;

import odoonto.domain.exceptions.DomainException;

public final class InvalidOdontogramException extends DomainException {

    public InvalidOdontogramException(final String message) {
        super(message);
    }

    public InvalidOdontogramException(final String message, final Throwable cause) {
        super(message, cause);
    }
} 