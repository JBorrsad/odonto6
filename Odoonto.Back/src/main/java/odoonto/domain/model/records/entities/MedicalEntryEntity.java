package odoonto.domain.model.records.entities;

import org.jmolecules.ddd.annotation.Entity;

import odoonto.domain.model.records.valueobjects.TimestampValue;
import odoonto.domain.model.staff.valueobjects.DoctorId;

import java.util.Objects;

@Entity
public class MedicalEntryEntity {
    private final String entryId;
    private final String notes;
    private final DoctorId doctorId;
    private final TimestampValue createdAt;

    public MedicalEntryEntity(final String entryId,
                             final String notes,
                             final DoctorId doctorId,
                             final TimestampValue createdAt) {
        if (entryId == null || entryId.trim().isEmpty()) {
            throw new IllegalArgumentException("Entry ID cannot be null or empty");
        }
        if (notes == null || notes.trim().isEmpty()) {
            throw new IllegalArgumentException("Notes cannot be null or empty");
        }
        if (doctorId == null) {
            throw new IllegalArgumentException("Doctor ID cannot be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created at cannot be null");
        }

        this.entryId = entryId.trim();
        this.notes = notes.trim();
        this.doctorId = doctorId;
        this.createdAt = createdAt;
    }

    public String getEntryId() {
        return entryId;
    }

    public String getNotes() {
        return notes;
    }

    public DoctorId getDoctorId() {
        return doctorId;
    }

    public TimestampValue getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final MedicalEntryEntity that = (MedicalEntryEntity) obj;
        return Objects.equals(entryId, that.entryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryId);
    }
} 