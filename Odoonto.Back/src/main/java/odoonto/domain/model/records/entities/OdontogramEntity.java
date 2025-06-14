package odoonto.domain.model.records.entities;

import org.jmolecules.ddd.annotation.Entity;

import odoonto.domain.model.records.valueobjects.ToothNumber;
import odoonto.domain.model.catalog.valueobjects.TreatmentId;
import odoonto.domain.exceptions.DomainException;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

@Entity
public class OdontogramEntity {
    
    private final Map<ToothNumber, ToothEntity> teeth;

    private OdontogramEntity(final Map<ToothNumber, ToothEntity> teeth) {
        this.teeth = new HashMap<>(teeth);
    }

    public static OdontogramEntity createEmpty() {
        final Map<ToothNumber, ToothEntity> initialTeeth = new HashMap<>();
        
        for (int toothNumber = 1; toothNumber <= 32; toothNumber++) {
            final ToothNumber number = ToothNumber.of(toothNumber);
            final ToothEntity tooth = ToothEntity.createHealthy(number);
            initialTeeth.put(number, tooth);
        }
        
        return new OdontogramEntity(initialTeeth);
    }

    public static OdontogramEntity reconstituteFromPersistence(
            final Map<ToothNumber, ToothEntity> teeth) {
        validateReconstitutionParameters(teeth);
        return new OdontogramEntity(teeth);
    }

    public void recordTreatment(final ToothNumber toothNumber,
                               final TreatmentId treatmentId) {
        validateToothNumber(toothNumber);
        validateTreatmentId(treatmentId);
        
        final ToothEntity tooth = teeth.get(toothNumber);
        if (tooth == null) {
            throw new DomainException("Tooth not found: " + toothNumber);
        }
        
        tooth.addTreatment(treatmentId);
    }

    public void recordLesion(final ToothNumber toothNumber,
                            final String lesionType) {
        validateToothNumber(toothNumber);
        validateLesionType(lesionType);
        
        final ToothEntity tooth = teeth.get(toothNumber);
        if (tooth == null) {
            throw new DomainException("Tooth not found: " + toothNumber);
        }
        
        tooth.addLesion(lesionType);
    }

    public boolean hasActiveTreatmentForTooth(final ToothNumber toothNumber) {
        validateToothNumber(toothNumber);
        
        final ToothEntity tooth = teeth.get(toothNumber);
        return tooth != null && tooth.hasActiveTreatments();
    }

    public boolean hasLesionsForTooth(final ToothNumber toothNumber) {
        validateToothNumber(toothNumber);
        
        final ToothEntity tooth = teeth.get(toothNumber);
        return tooth != null && tooth.hasLesions();
    }

    public Optional<ToothEntity> getTooth(final ToothNumber toothNumber) {
        validateToothNumber(toothNumber);
        return Optional.ofNullable(teeth.get(toothNumber));
    }

    public List<ToothNumber> getTeethWithLesions() {
        return teeth.values().stream()
                .filter(ToothEntity::hasLesions)
                .map(ToothEntity::getToothNumber)
                .toList();
    }

    public List<ToothNumber> getTeethWithTreatments() {
        return teeth.values().stream()
                .filter(ToothEntity::hasActiveTreatments)
                .map(ToothEntity::getToothNumber)
                .toList();
    }

    public int getHealthyTeethCount() {
        return (int) teeth.values().stream()
                .filter(ToothEntity::isHealthy)
                .count();
    }

    public int getAffectedTeethCount() {
        return (int) teeth.values().stream()
                .filter(tooth -> !tooth.isHealthy())
                .count();
    }

    private static void validateReconstitutionParameters(final Map<ToothNumber, ToothEntity> teeth) {
        if (teeth == null) {
            throw new DomainException("Teeth map cannot be null");
        }
        if (teeth.size() != 32) {
            throw new DomainException("Odontogram must contain exactly 32 teeth");
        }
    }

    private static void validateToothNumber(final ToothNumber toothNumber) {
        if (toothNumber == null) {
            throw new DomainException("Tooth number cannot be null");
        }
    }

    private static void validateTreatmentId(final TreatmentId treatmentId) {
        if (treatmentId == null) {
            throw new DomainException("Treatment ID cannot be null");
        }
    }

    private static void validateLesionType(final String lesionType) {
        if (lesionType == null || lesionType.trim().isEmpty()) {
            throw new DomainException("Lesion type cannot be null or empty");
        }
    }

    public Map<ToothNumber, ToothEntity> getAllTeeth() {
        return new HashMap<>(teeth);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final OdontogramEntity that = (OdontogramEntity) other;
        return Objects.equals(teeth, that.teeth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teeth);
    }
} 