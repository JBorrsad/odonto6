package odoonto.domain.model.records.aggregates;

import org.jmolecules.ddd.annotation.AggregateRoot;

import odoonto.domain.model.records.valueobjects.MedicalRecordId;
import odoonto.domain.model.records.entities.MedicalEntryEntity;
import odoonto.domain.model.records.entities.OdontogramEntity;
import odoonto.domain.model.patients.valueobjects.PatientId;
import odoonto.domain.model.records.valueobjects.ToothNumber;
import odoonto.domain.model.records.valueobjects.TreatmentId;
import odoonto.domain.events.records.TreatmentCompletedEvent;
import odoonto.domain.model.shared.valueobjects.TimestampValue;
import odoonto.domain.exceptions.DomainException;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@AggregateRoot
public class MedicalRecordAggregate {
    
    private final MedicalRecordId medicalRecordId;
    private final PatientId patientId;
    private final List<MedicalEntryEntity> medicalEntries;
    private final OdontogramEntity odontogram;

    public MedicalRecordAggregate(final PatientId patientId) {
        validateConstructorParameters(patientId);
        
        this.medicalRecordId = MedicalRecordId.fromPatientId(patientId);
        this.patientId = patientId;
        this.medicalEntries = new ArrayList<>();
        this.odontogram = OdontogramEntity.createEmpty();
    }

    private MedicalRecordAggregate(final MedicalRecordId medicalRecordId,
                                  final PatientId patientId,
                                  final List<MedicalEntryEntity> medicalEntries,
                                  final OdontogramEntity odontogram) {
        this.medicalRecordId = medicalRecordId;
        this.patientId = patientId;
        this.medicalEntries = new ArrayList<>(medicalEntries);
        this.odontogram = odontogram;
    }

    public static MedicalRecordAggregate reconstituteFromPersistence(
            final MedicalRecordId medicalRecordId,
            final PatientId patientId,
            final List<MedicalEntryEntity> medicalEntries,
            final OdontogramEntity odontogram) {
        
        validateReconstitutionParameters(medicalRecordId, patientId, odontogram);
        
        return new MedicalRecordAggregate(medicalRecordId, patientId, 
                                         medicalEntries != null ? medicalEntries : new ArrayList<>(), 
                                         odontogram);
    }

    public void registerTreatment(final PatientId treatmentPatientId,
                                 final ToothNumber toothNumber,
                                 final TreatmentId treatmentId,
                                 final String treatmentNotes) {
        validatePatientMatch(treatmentPatientId);
        validateTreatmentParameters(toothNumber, treatmentId);
        
        final MedicalEntryEntity medicalEntry = MedicalEntryEntity.createTreatmentEntry(
            toothNumber, treatmentId, treatmentNotes, TimestampValue.now());
        
        medicalEntries.add(medicalEntry);
        odontogram.recordTreatment(toothNumber, treatmentId);
    }

    public void addClinicalNote(final String clinicalNote) {
        if (clinicalNote == null || clinicalNote.trim().isEmpty()) {
            throw new DomainException("Clinical note cannot be null or empty");
        }
        
        final MedicalEntryEntity noteEntry = MedicalEntryEntity.createClinicalNote(
            clinicalNote, TimestampValue.now());
        
        medicalEntries.add(noteEntry);
    }

    public void recordLesion(final ToothNumber toothNumber,
                            final String lesionType,
                            final String lesionDescription) {
        validateLesionParameters(toothNumber, lesionType);
        
        final MedicalEntryEntity lesionEntry = MedicalEntryEntity.createLesionEntry(
            toothNumber, lesionType, lesionDescription, TimestampValue.now());
        
        medicalEntries.add(lesionEntry);
        odontogram.recordLesion(toothNumber, lesionType);
    }

    public List<MedicalEntryEntity> getTreatmentHistory(final ToothNumber toothNumber) {
        if (toothNumber == null) {
            throw new DomainException("Tooth number cannot be null");
        }
        
        return medicalEntries.stream()
                .filter(entry -> entry.affectsTooth(toothNumber))
                .filter(MedicalEntryEntity::isTreatmentEntry)
                .toList();
    }

    public List<MedicalEntryEntity> getRecentEntries(final int maxEntries) {
        if (maxEntries <= 0) {
            throw new DomainException("Max entries must be positive");
        }
        
        return medicalEntries.stream()
                .sorted((e1, e2) -> e2.getTimestamp().compareTo(e1.getTimestamp()))
                .limit(maxEntries)
                .toList();
    }

    public Optional<MedicalEntryEntity> getLastTreatmentForTooth(final ToothNumber toothNumber) {
        return getTreatmentHistory(toothNumber).stream()
                .max((e1, e2) -> e1.getTimestamp().compareTo(e2.getTimestamp()));
    }

    public boolean hasActiveTreatmentForTooth(final ToothNumber toothNumber) {
        return odontogram.hasActiveTreatmentForTooth(toothNumber);
    }

    public void processCompletedTreatment(final TreatmentCompletedEvent event) {
        if (event == null) {
            throw new DomainException("Treatment completed event cannot be null");
        }
        
        validatePatientMatch(event.getPatientId());
        
        for (final ToothNumber toothNumber : event.getAffectedTeeth()) {
            final MedicalEntryEntity treatmentEntry = MedicalEntryEntity.createTreatmentEntry(
                toothNumber, 
                event.getTreatmentId(), 
                event.getTreatmentName() + " - " + event.getNotes(),
                event.getCompletedAt()
            );
            
            medicalEntries.add(treatmentEntry);
        }
    }

    private void validatePatientMatch(final PatientId treatmentPatientId) {
        if (!this.patientId.equals(treatmentPatientId)) {
            throw new DomainException("Treatment patient ID does not match record patient ID");
        }
    }

    private static void validateConstructorParameters(final PatientId patientId) {
        if (patientId == null) {
            throw new DomainException("Patient ID cannot be null");
        }
    }

    private static void validateReconstitutionParameters(final MedicalRecordId medicalRecordId,
                                                        final PatientId patientId,
                                                        final OdontogramEntity odontogram) {
        if (medicalRecordId == null) {
            throw new DomainException("Medical record ID cannot be null");
        }
        if (odontogram == null) {
            throw new DomainException("Odontogram cannot be null");
        }
        validateConstructorParameters(patientId);
    }

    private static void validateTreatmentParameters(final ToothNumber toothNumber,
                                                   final TreatmentId treatmentId) {
        if (toothNumber == null) {
            throw new DomainException("Tooth number cannot be null");
        }
        if (treatmentId == null) {
            throw new DomainException("Treatment ID cannot be null");
        }
    }

    private static void validateLesionParameters(final ToothNumber toothNumber,
                                                final String lesionType) {
        if (toothNumber == null) {
            throw new DomainException("Tooth number cannot be null");
        }
        if (lesionType == null || lesionType.trim().isEmpty()) {
            throw new DomainException("Lesion type cannot be null or empty");
        }
    }

    public MedicalRecordId getMedicalRecordId() {
        return medicalRecordId;
    }

    public PatientId getPatientId() {
        return patientId;
    }

    public List<MedicalEntryEntity> getMedicalEntries() {
        return Collections.unmodifiableList(medicalEntries);
    }

    public OdontogramEntity getOdontogram() {
        return odontogram;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final MedicalRecordAggregate that = (MedicalRecordAggregate) other;
        return Objects.equals(medicalRecordId, that.medicalRecordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(medicalRecordId);
    }
} 