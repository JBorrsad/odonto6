package odoonto.domain.model.shared.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;
import java.math.BigDecimal;
import java.util.Objects;

@ValueObject
public final class MoneyValue {
    
    private final BigDecimal amount;
    private final String currency;

    public MoneyValue(final BigDecimal amount, final String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
        this.amount = amount;
        this.currency = currency.trim().toUpperCase();
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
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final MoneyValue that = (MoneyValue) obj;
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