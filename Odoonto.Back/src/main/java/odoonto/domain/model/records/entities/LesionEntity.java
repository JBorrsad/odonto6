package odoonto.domain.model.records.entities;

import org.jmolecules.ddd.annotation.Entity;

import odoonto.domain.model.records.valueobjects.LesionType;
import odoonto.domain.model.shared.valueobjects.TimestampValue;

import java.util.Objects;

@Entity
public final class LesionEntity {
    
    private final String lesionId;
    private final LesionType lesionType;
    private final String severity;
    private final String location;
    private final String observations;
    private final TimestampValue detectedAt;
    private final boolean requiresTreatment;

    private LesionEntity(final String lesionId, 
                        final LesionType lesionType,
                        final String severity,
                        final String location,
                        final String observations,
                        final TimestampValue detectedAt,
                        final boolean requiresTreatment) {
        validateParameters(lesionId, lesionType, severity, location, detectedAt);
        this.lesionId = lesionId;
        this.lesionType = lesionType;
        this.severity = severity;
        this.location = location;
        this.observations = observations != null ? observations : "";
        this.detectedAt = detectedAt;
        this.requiresTreatment = requiresTreatment;
    }

    public static LesionEntity create(final String lesionId,
                                     final LesionType lesionType,
                                     final String severity,
                                     final String location,
                                     final String observations,
                                     final boolean requiresTreatment) {
        return new LesionEntity(lesionId, lesionType, severity, location, 
                               observations, TimestampValue.now(), requiresTreatment);
    }

    public static LesionEntity reconstruct(final String lesionId,
                                          final LesionType lesionType,
                                          final String severity,
                                          final String location,
                                          final String observations,
                                          final TimestampValue detectedAt,
                                          final boolean requiresTreatment) {
        return new LesionEntity(lesionId, lesionType, severity, location, 
                               observations, detectedAt, requiresTreatment);
    }

    public boolean isUrgent() {
        return lesionType.requiresUrgentTreatment() || "SEVERE".equalsIgnoreCase(severity);
    }

    public boolean isMild() {
        return "MILD".equalsIgnoreCase(severity);
    }

    public boolean isModerate() {
        return "MODERATE".equalsIgnoreCase(severity);
    }

    public boolean isSevere() {
        return "SEVERE".equalsIgnoreCase(severity);
    }

    public boolean needsImmediateAttention() {
        return isUrgent() || lesionType.isInfection();
    }

    public boolean canWaitForTreatment() {
        return !requiresTreatment || (isMild() && !lesionType.isInfection());
    }

    private static void validateParameters(final String lesionId,
                                          final LesionType lesionType,
                                          final String severity,
                                          final String location,
                                          final TimestampValue detectedAt) {
        if (lesionId == null || lesionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lesion ID cannot be null or empty");
        }
        if (lesionType == null) {
            throw new IllegalArgumentException("Lesion type cannot be null");
        }
        if (severity == null || severity.trim().isEmpty()) {
            throw new IllegalArgumentException("Severity cannot be null or empty");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be null or empty");
        }
        if (detectedAt == null) {
            throw new IllegalArgumentException("Detection timestamp cannot be null");
        }
    }

    public String getLesionId() {
        return lesionId;
    }

    public LesionType getLesionType() {
        return lesionType;
    }

    public String getSeverity() {
        return severity;
    }

    public String getLocation() {
        return location;
    }

    public String getObservations() {
        return observations;
    }

    public TimestampValue getDetectedAt() {
        return detectedAt;
    }

    public boolean isRequiresTreatment() {
        return requiresTreatment;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final LesionEntity that = (LesionEntity) other;
        return Objects.equals(lesionId, that.lesionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lesionId);
    }

    @Override
    public String toString() {
        return "LesionEntity{" +
                "id='" + lesionId + '\'' +
                ", type=" + lesionType +
                ", severity='" + severity + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
} 