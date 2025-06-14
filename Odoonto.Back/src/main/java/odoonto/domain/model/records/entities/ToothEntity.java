package odoonto.domain.model.records.entities;

import org.jmolecules.ddd.annotation.Entity;

import odoonto.domain.model.records.valueobjects.ToothNumber;
import odoonto.domain.model.catalog.valueobjects.TreatmentId;
import odoonto.domain.exceptions.DomainException;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

@Entity
public class ToothEntity {
    
    private final ToothNumber toothNumber;
    private final List<LesionEntity> lesions;
    private final List<TreatmentEntity> treatments;

    private ToothEntity(final ToothNumber toothNumber,
                       final List<LesionEntity> lesions,
                       final List<TreatmentEntity> treatments) {
        this.toothNumber = toothNumber;
        this.lesions = new ArrayList<>(lesions);
        this.treatments = new ArrayList<>(treatments);
    }

    public static ToothEntity createHealthy(final ToothNumber toothNumber) {
        validateToothNumber(toothNumber);
        return new ToothEntity(toothNumber, new ArrayList<>(), new ArrayList<>());
    }

    public static ToothEntity reconstituteFromPersistence(
            final ToothNumber toothNumber,
            final List<LesionEntity> lesions,
            final List<TreatmentEntity> treatments) {
        validateToothNumber(toothNumber);
        
        return new ToothEntity(toothNumber,
                              lesions != null ? lesions : new ArrayList<>(),
                              treatments != null ? treatments : new ArrayList<>());
    }

    public void addLesion(final String lesionType) {
        validateLesionType(lesionType);
        
        final LesionEntity lesion = LesionEntity.create(lesionType);
        lesions.add(lesion);
    }

    public void addTreatment(final TreatmentId treatmentId) {
        validateTreatmentId(treatmentId);
        
        final TreatmentEntity treatment = TreatmentEntity.create(treatmentId);
        treatments.add(treatment);
    }

    public void completeTreatment(final TreatmentId treatmentId) {
        validateTreatmentId(treatmentId);
        
        treatments.stream()
                .filter(treatment -> treatment.getTreatmentId().equals(treatmentId))
                .forEach(TreatmentEntity::markAsCompleted);
    }

    public boolean hasActiveTreatments() {
        return treatments.stream().anyMatch(TreatmentEntity::isActive);
    }

    public boolean hasLesions() {
        return !lesions.isEmpty();
    }

    public boolean isHealthy() {
        return lesions.isEmpty() && treatments.stream().noneMatch(TreatmentEntity::isActive);
    }

    public boolean hasTreatment(final TreatmentId treatmentId) {
        return treatments.stream()
                .anyMatch(treatment -> treatment.getTreatmentId().equals(treatmentId));
    }

    public boolean hasLesionType(final String lesionType) {
        return lesions.stream()
                .anyMatch(lesion -> lesion.getLesionType().equals(lesionType));
    }

    public int getActiveTreatmentCount() {
        return (int) treatments.stream().filter(TreatmentEntity::isActive).count();
    }

    public int getLesionCount() {
        return lesions.size();
    }

    private static void validateToothNumber(final ToothNumber toothNumber) {
        if (toothNumber == null) {
            throw new DomainException("Tooth number cannot be null");
        }
    }

    private static void validateLesionType(final String lesionType) {
        if (lesionType == null || lesionType.trim().isEmpty()) {
            throw new DomainException("Lesion type cannot be null or empty");
        }
    }

    private static void validateTreatmentId(final TreatmentId treatmentId) {
        if (treatmentId == null) {
            throw new DomainException("Treatment ID cannot be null");
        }
    }

    public ToothNumber getToothNumber() {
        return toothNumber;
    }

    public List<LesionEntity> getLesions() {
        return Collections.unmodifiableList(lesions);
    }

    public List<TreatmentEntity> getTreatments() {
        return Collections.unmodifiableList(treatments);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final ToothEntity that = (ToothEntity) other;
        return Objects.equals(toothNumber, that.toothNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toothNumber);
    }
} 