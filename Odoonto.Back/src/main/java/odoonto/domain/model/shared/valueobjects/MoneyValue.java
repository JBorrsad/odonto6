package odoonto.domain.model.shared.valueobjects;

import java.math.BigDecimal;
import java.util.Objects;

public final class MoneyValue {
    
    private final BigDecimal amount;
    private final String currency;

    private MoneyValue(final BigDecimal amount, final String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public static MoneyValue of(final BigDecimal amount, final String currency) {
        return new MoneyValue(amount, currency);
    }

    public static MoneyValue cop(final BigDecimal amount) {
        return new MoneyValue(amount, "COP");
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final MoneyValue that = (MoneyValue) other;
        return Objects.equals(amount, that.amount) && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
} 