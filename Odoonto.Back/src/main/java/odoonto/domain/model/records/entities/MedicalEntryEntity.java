package odoonto.domain.model.records.entities;

import org.jmolecules.ddd.annotation.Entity;

import odoonto.domain.model.records.valueobjects.ToothNumber;
import odoonto.domain.model.catalog.valueobjects.TreatmentId;
import odoonto.domain.model.shared.valueobjects.TimestampValue;
import odoonto.domain.exceptions.DomainException;

import java.util.Objects;
import java.util.Optional;

@Entity
public class MedicalEntryEntity {
    
    private final String entryType;
    private final ToothNumber affectedTooth;
    private final TreatmentId treatmentId;
    private final String lesionType;
    private final String clinicalNotes;
    private final String description;
    private final TimestampValue timestamp;

    private MedicalEntryEntity(final String entryType,
                              final ToothNumber affectedTooth,
                              final TreatmentId treatmentId,
                              final String lesionType,
                              final String clinicalNotes,
                              final String description,
                              final TimestampValue timestamp) {
        this.entryType = entryType;
        this.affectedTooth = affectedTooth;
        this.treatmentId = treatmentId;
        this.lesionType = lesionType;
        this.clinicalNotes = clinicalNotes;
        this.description = description;
        this.timestamp = timestamp;
    }

    public static MedicalEntryEntity createTreatmentEntry(
            final ToothNumber toothNumber,
            final TreatmentId treatmentId,
            final String treatmentNotes,
            final TimestampValue timestamp) {
        validateTreatmentEntryParameters(toothNumber, treatmentId, timestamp);
        
        return new MedicalEntryEntity("TREATMENT", toothNumber, treatmentId, 
                                     null, treatmentNotes, null, timestamp);
    }

    public static MedicalEntryEntity createLesionEntry(
            final ToothNumber toothNumber,
            final String lesionType,
            final String lesionDescription,
            final TimestampValue timestamp) {
        validateLesionEntryParameters(toothNumber, lesionType, timestamp);
        
        return new MedicalEntryEntity("LESION", toothNumber, null, 
                                     lesionType, null, lesionDescription, timestamp);
    }

    public static MedicalEntryEntity createClinicalNote(
            final String clinicalNote,
            final TimestampValue timestamp) {
        validateClinicalNoteParameters(clinicalNote, timestamp);
        
        return new MedicalEntryEntity("CLINICAL_NOTE", null, null, 
                                     null, clinicalNote, null, timestamp);
    }

    public static MedicalEntryEntity reconstituteFromPersistence(
            final String entryType,
            final ToothNumber affectedTooth,
            final TreatmentId treatmentId,
            final String lesionType,
            final String clinicalNotes,
            final String description,
            final TimestampValue timestamp) {
        validateReconstitutionParameters(entryType, timestamp);
        
        return new MedicalEntryEntity(entryType, affectedTooth, treatmentId,
                                     lesionType, clinicalNotes, description, timestamp);
    }

    public boolean affectsTooth(final ToothNumber toothNumber) {
        return this.affectedTooth != null && this.affectedTooth.equals(toothNumber);
    }

    public boolean isTreatmentEntry() {
        return "TREATMENT".equals(entryType);
    }

    public boolean isLesionEntry() {
        return "LESION".equals(entryType);
    }

    public boolean isClinicalNote() {
        return "CLINICAL_NOTE".equals(entryType);
    }

    public boolean involvesTreatment(final TreatmentId treatmentId) {
        return this.treatmentId != null && this.treatmentId.equals(treatmentId);
    }

    public boolean hasLesionType(final String lesionType) {
        return this.lesionType != null && this.lesionType.equals(lesionType);
    }

    private static void validateTreatmentEntryParameters(final ToothNumber toothNumber,
                                                        final TreatmentId treatmentId,
                                                        final TimestampValue timestamp) {
        if (toothNumber == null) {
            throw new DomainException("Tooth number cannot be null for treatment entry");
        }
        if (treatmentId == null) {
            throw new DomainException("Treatment ID cannot be null for treatment entry");
        }
        if (timestamp == null) {
            throw new DomainException("Timestamp cannot be null");
        }
    }

    private static void validateLesionEntryParameters(final ToothNumber toothNumber,
                                                     final String lesionType,
                                                     final TimestampValue timestamp) {
        if (toothNumber == null) {
            throw new DomainException("Tooth number cannot be null for lesion entry");
        }
        if (lesionType == null || lesionType.trim().isEmpty()) {
            throw new DomainException("Lesion type cannot be null or empty");
        }
        if (timestamp == null) {
            throw new DomainException("Timestamp cannot be null");
        }
    }

    private static void validateClinicalNoteParameters(final String clinicalNote,
                                                      final TimestampValue timestamp) {
        if (clinicalNote == null || clinicalNote.trim().isEmpty()) {
            throw new DomainException("Clinical note cannot be null or empty");
        }
        if (timestamp == null) {
            throw new DomainException("Timestamp cannot be null");
        }
    }

    private static void validateReconstitutionParameters(final String entryType,
                                                        final TimestampValue timestamp) {
        if (entryType == null || entryType.trim().isEmpty()) {
            throw new DomainException("Entry type cannot be null or empty");
        }
        if (timestamp == null) {
            throw new DomainException("Timestamp cannot be null");
        }
    }

    public String getEntryType() {
        return entryType;
    }

    public Optional<ToothNumber> getAffectedTooth() {
        return Optional.ofNullable(affectedTooth);
    }

    public Optional<TreatmentId> getTreatmentId() {
        return Optional.ofNullable(treatmentId);
    }

    public Optional<String> getLesionType() {
        return Optional.ofNullable(lesionType);
    }

    public Optional<String> getClinicalNotes() {
        return Optional.ofNullable(clinicalNotes);
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public TimestampValue getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final MedicalEntryEntity that = (MedicalEntryEntity) other;
        return Objects.equals(entryType, that.entryType) &&
                Objects.equals(affectedTooth, that.affectedTooth) &&
                Objects.equals(treatmentId, that.treatmentId) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryType, affectedTooth, treatmentId, timestamp);
    }
} 