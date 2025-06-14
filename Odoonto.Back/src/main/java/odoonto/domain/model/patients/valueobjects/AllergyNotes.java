package odoonto.domain.model.patients.valueobjects;

import org.jmolecules.ddd.annotation.ValueObject;
import java.util.Objects;

@ValueObject
public final class AllergyNotes {
    private final String notes;

    public AllergyNotes(final String notes) {
        this.notes = notes != null ? notes.trim() : "";
    }

    public String getNotes() {
        return notes;
    }

    public boolean isEmpty() {
        return notes.isEmpty();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final AllergyNotes that = (AllergyNotes) obj;
        return Objects.equals(notes, that.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notes);
    }

    @Override
    public String toString() {
        return notes;
    }
} 