package odoonto.domain.exceptions.catalog;

import odoonto.domain.exceptions.DomainException;

public final class InvalidCatalogException extends DomainException {

    public InvalidCatalogException(final String message) {
        super(message);
    }

    public InvalidCatalogException(final String message, final Throwable cause) {
        super(message, cause);
    }
} 