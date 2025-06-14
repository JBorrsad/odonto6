package odoonto.domain.model.records.entities;

import org.jmolecules.ddd.annotation.Entity;

import odoonto.domain.model.records.valueobjects.TreatmentId;
import odoonto.domain.model.records.valueobjects.ToothNumber;
import odoonto.domain.model.records.valueobjects.TreatmentType;
import odoonto.domain.model.records.valueobjects.TimestampValue;
import odoonto.domain.model.shared.valueobjects.MoneyValue;

@Entity
public class TreatmentEntity {
    private final TreatmentId treatmentId;
    private final ToothNumber toothNumber;
    private final TreatmentType treatmentType;
    private final MoneyValue cost;
    private final TimestampValue performedAt;
    private final String notes;

    public TreatmentEntity(final TreatmentId treatmentId,
                          final ToothNumber toothNumber,
                          final TreatmentType treatmentType,
                          final MoneyValue cost,
                          final TimestampValue performedAt,
                          final String notes) {
        this.treatmentId = treatmentId;
        this.toothNumber = toothNumber;
        this.treatmentType = treatmentType;
        this.cost = cost;
        this.performedAt = performedAt;
        this.notes = notes;
    }

    public TreatmentId getTreatmentId() {
        return treatmentId;
    }

    public ToothNumber getToothNumber() {
        return toothNumber;
    }

    public TreatmentType getTreatmentType() {
        return treatmentType;
    }

    public MoneyValue getCost() {
        return cost;
    }

    public TimestampValue getPerformedAt() {
        return performedAt;
    }

    public String getNotes() {
        return notes;
    }
} 