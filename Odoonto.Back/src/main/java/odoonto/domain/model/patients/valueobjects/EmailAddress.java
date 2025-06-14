package odoonto.domain.model.patients.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;
import odoonto.domain.exceptions.patients.InvalidEmailException;
import java.util.Objects;
import java.util.regex.Pattern;

@ValueObject
public final class EmailAddress {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private final String value;

    public EmailAddress(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidEmailException("Email cannot be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(value.trim()).matches()) {
            throw new InvalidEmailException("Invalid email format");
        }
        this.value = value.trim().toLowerCase();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final EmailAddress that = (EmailAddress) obj;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
} 