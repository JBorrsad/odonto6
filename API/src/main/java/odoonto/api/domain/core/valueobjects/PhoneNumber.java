package odoonto.api.domain.core.valueobjects;


public record PhoneNumber(String value) {
    public PhoneNumber {
        if (!value.matches("\\+?[0-9]{7,15}"))
            throw new IllegalArgumentException("Teléfono inválido");
    }
}
