package odoonto.api.domain.core.valueobjects;

public record EmailAddress(String value) {
    public EmailAddress {
        if (!value.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
            throw new IllegalArgumentException("Email inválido");
    }
}
