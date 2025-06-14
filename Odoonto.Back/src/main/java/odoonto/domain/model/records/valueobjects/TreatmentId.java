package odoonto.domain.model.records.valueobjects;

import java.util.Objects;
import java.util.UUID;

public final class TreatmentId {
    
    private final String id;

    private TreatmentId(final String id) {
        validateId(id);
        this.id = id;
    }

    public static TreatmentId generate() {
        return new TreatmentId(UUID.randomUUID().toString());
    }

    public static TreatmentId of(final String id) {
        return new TreatmentId(id);
    }

    private static void validateId(final String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment ID cannot be null or empty");
        }
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final TreatmentId that = (TreatmentId) other;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }
} 