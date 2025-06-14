package odoonto.domain.model.records.entities;

import org.jmolecules.ddd.annotation.Entity;

import odoonto.domain.model.catalog.valueobjects.TreatmentType;
import odoonto.domain.model.shared.valueobjects.TimestampValue;
import odoonto.domain.model.shared.valueobjects.MoneyValue;

import java.util.Objects;

@Entity
public final class TreatmentEntity {
    
    private final String treatmentId;
    private final TreatmentType treatmentType;
    private final String treatmentName;
    private final String description;
    private final MoneyValue cost;
    private final TimestampValue appliedAt;
    private final String doctorId;
    private final String status;
    private final String notes;

    private TreatmentEntity(final String treatmentId,
                           final TreatmentType treatmentType,
                           final String treatmentName,
                           final String description,
                           final MoneyValue cost,
                           final TimestampValue appliedAt,
                           final String doctorId,
                           final String status,
                           final String notes) {
        validateParameters(treatmentId, treatmentType, treatmentName, appliedAt, doctorId, status);
        this.treatmentId = treatmentId;
        this.treatmentType = treatmentType;
        this.treatmentName = treatmentName;
        this.description = description != null ? description : "";
        this.cost = cost != null ? cost : MoneyValue.zeroPesos();
        this.appliedAt = appliedAt;
        this.doctorId = doctorId;
        this.status = status;
        this.notes = notes != null ? notes : "";
    }

    public static TreatmentEntity create(final String treatmentId,
                                        final TreatmentType treatmentType,
                                        final String treatmentName,
                                        final String description,
                                        final MoneyValue cost,
                                        final String doctorId,
                                        final String notes) {
        return new TreatmentEntity(treatmentId, treatmentType, treatmentName, 
                                  description, cost, TimestampValue.now(), 
                                  doctorId, "COMPLETED", notes);
    }

    public static TreatmentEntity reconstruct(final String treatmentId,
                                             final TreatmentType treatmentType,
                                             final String treatmentName,
                                             final String description,
                                             final MoneyValue cost,
                                             final TimestampValue appliedAt,
                                             final String doctorId,
                                             final String status,
                                             final String notes) {
        return new TreatmentEntity(treatmentId, treatmentType, treatmentName, 
                                  description, cost, appliedAt, doctorId, status, notes);
    }

    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(status);
    }

    public boolean isInProgress() {
        return "IN_PROGRESS".equalsIgnoreCase(status);
    }

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(status);
    }

    public boolean isExpensive() {
        return cost != null && cost.getAmount().doubleValue() > 500000.0;
    }

    public boolean isPreventive() {
        return treatmentType.isPreventive();
    }

    public boolean isRestorative() {
        return treatmentType.isRestorative();
    }

    public boolean requiresSpecialist() {
        return treatmentType.requiresSpecialist();
    }

    public boolean isBasicTreatment() {
        return treatmentType.isBasicTreatment();
    }

    private static void validateParameters(final String treatmentId,
                                          final TreatmentType treatmentType,
                                          final String treatmentName,
                                          final TimestampValue appliedAt,
                                          final String doctorId,
                                          final String status) {
        if (treatmentId == null || treatmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment ID cannot be null or empty");
        }
        if (treatmentType == null) {
            throw new IllegalArgumentException("Treatment type cannot be null");
        }
        if (treatmentName == null || treatmentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Treatment name cannot be null or empty");
        }
        if (appliedAt == null) {
            throw new IllegalArgumentException("Applied timestamp cannot be null");
        }
        if (doctorId == null || doctorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Doctor ID cannot be null or empty");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
    }

    public String getTreatmentId() {
        return treatmentId;
    }

    public TreatmentType getTreatmentType() {
        return treatmentType;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public String getDescription() {
        return description;
    }

    public MoneyValue getCost() {
        return cost;
    }

    public TimestampValue getAppliedAt() {
        return appliedAt;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final TreatmentEntity that = (TreatmentEntity) other;
        return Objects.equals(treatmentId, that.treatmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(treatmentId);
    }

    @Override
    public String toString() {
        return "TreatmentEntity{" +
                "id='" + treatmentId + '\'' +
                ", name='" + treatmentName + '\'' +
                ", type=" + treatmentType +
                ", status='" + status + '\'' +
                ", cost=" + cost +
                '}';
    }
} 