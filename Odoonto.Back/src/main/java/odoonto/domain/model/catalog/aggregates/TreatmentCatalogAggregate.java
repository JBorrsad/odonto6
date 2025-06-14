package odoonto.domain.model.catalog.aggregates;

import org.jmolecules.ddd.annotation.AggregateRoot;

import odoonto.domain.events.shared.DomainEvents;
import odoonto.domain.events.catalog.TreatmentPriceUpdatedEvent;
import odoonto.domain.model.catalog.entities.TreatmentPricingEntity;
import odoonto.domain.model.catalog.valueobjects.CatalogId;
import odoonto.domain.model.records.valueobjects.TreatmentId;
import odoonto.domain.model.shared.valueobjects.MoneyValue;
import odoonto.domain.model.shared.valueobjects.DurationValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AggregateRoot
public class TreatmentCatalogAggregate {
    private final CatalogId catalogId;
    private final String catalogName;
    private final List<TreatmentPricingEntity> treatmentPricings;

    public TreatmentCatalogAggregate(final CatalogId catalogId,
                                   final String catalogName,
                                   final List<TreatmentPricingEntity> treatmentPricings) {
        this.catalogId = catalogId;
        this.catalogName = catalogName;
        this.treatmentPricings = new ArrayList<>(treatmentPricings);
    }

    public void updateTreatmentPrice(final TreatmentId treatmentId, 
                                   final MoneyValue newPrice) {
        final Optional<TreatmentPricingEntity> treatmentPricing = findTreatmentPricing(treatmentId);
        
        if (treatmentPricing.isPresent()) {
            final TreatmentPricingEntity updatedPricing = treatmentPricing.get().updatePrice(newPrice);
            replaceTreatmentPricing(treatmentPricing.get(), updatedPricing);
            
            DomainEvents.raise(TreatmentPriceUpdatedEvent.create(
                this.catalogId,
                treatmentId,
                newPrice
            ));
        }
    }

    public Optional<MoneyValue> getTreatmentPrice(final TreatmentId treatmentId) {
        return findTreatmentPricing(treatmentId)
            .map(TreatmentPricingEntity::getPrice);
    }

    public Optional<DurationValue> getTreatmentDuration(final TreatmentId treatmentId) {
        return findTreatmentPricing(treatmentId)
            .map(TreatmentPricingEntity::getEstimatedDuration);
    }

    private Optional<TreatmentPricingEntity> findTreatmentPricing(final TreatmentId treatmentId) {
        return treatmentPricings.stream()
            .filter(pricing -> pricing.getTreatmentId().equals(treatmentId))
            .findFirst();
    }

    private void replaceTreatmentPricing(final TreatmentPricingEntity oldPricing, 
                                       final TreatmentPricingEntity newPricing) {
        final int index = treatmentPricings.indexOf(oldPricing);
        if (index >= 0) {
            treatmentPricings.set(index, newPricing);
        }
    }

    public CatalogId getCatalogId() {
        return catalogId;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public List<TreatmentPricingEntity> getTreatmentPricings() {
        return new ArrayList<>(treatmentPricings);
    }
} 