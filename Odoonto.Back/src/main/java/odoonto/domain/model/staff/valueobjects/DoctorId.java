package odoonto.domain.model.staff.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;
import java.util.Objects;
import java.util.UUID;

@ValueObject
public final class DoctorId {
    private final String value;

    private DoctorId(final String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("DoctorId cannot be null or empty");
        }
        this.value = value.trim();
    }

    public static DoctorId generate() {
        return new DoctorId(UUID.randomUUID().toString());
    }

    public static DoctorId of(final String value) {
        return new DoctorId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final DoctorId doctorId = (DoctorId) obj;
        return Objects.equals(value, doctorId.value);
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