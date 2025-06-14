package odoonto.domain.model.catalog.entities;

import org.jmolecules.ddd.annotation.Entity;

import odoonto.domain.model.records.valueobjects.TreatmentId;
import odoonto.domain.model.shared.valueobjects.MoneyValue;
import odoonto.domain.model.shared.valueobjects.DurationValue;

@Entity
public class TreatmentPricingEntity {
    private final TreatmentId treatmentId;
    private final MoneyValue price;
    private final DurationValue estimatedDuration;
    private final String description;

    public TreatmentPricingEntity(final TreatmentId treatmentId,
                                 final MoneyValue price,
                                 final DurationValue estimatedDuration,
                                 final String description) {
        this.treatmentId = treatmentId;
        this.price = price;
        this.estimatedDuration = estimatedDuration;
        this.description = description;
    }

    public TreatmentPricingEntity updatePrice(final MoneyValue newPrice) {
        return new TreatmentPricingEntity(
            this.treatmentId,
            newPrice,
            this.estimatedDuration,
            this.description
        );
    }

    public TreatmentPricingEntity updateDuration(final DurationValue newDuration) {
        return new TreatmentPricingEntity(
            this.treatmentId,
            this.price,
            newDuration,
            this.description
        );
    }

    public TreatmentId getTreatmentId() {
        return treatmentId;
    }

    public MoneyValue getPrice() {
        return price;
    }

    public DurationValue getEstimatedDuration() {
        return estimatedDuration;
    }

    public String getDescription() {
        return description;
    }
} 