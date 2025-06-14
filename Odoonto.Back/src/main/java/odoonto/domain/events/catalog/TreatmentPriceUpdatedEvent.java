package odoonto.domain.events.catalog;

import odoonto.domain.events.shared.DomainEvent;
import odoonto.domain.model.shared.valueobjects.EventId;
import odoonto.domain.model.shared.valueobjects.TimestampValue;
import odoonto.domain.model.catalog.valueobjects.CatalogId;
import odoonto.domain.model.records.valueobjects.TreatmentId;
import odoonto.domain.model.shared.valueobjects.MoneyValue;

public record TreatmentPriceUpdatedEvent(
    EventId id,
    TimestampValue occurredAt,
    CatalogId catalogId,
    TreatmentId treatmentId,
    MoneyValue newPrice
) implements DomainEvent {

    @Override
    public EventId getEventId() {
        return id;
    }

    @Override
    public TimestampValue getOccurredAt() {
        return occurredAt;
    }

    public static TreatmentPriceUpdatedEvent create(final CatalogId catalogId,
                                                  final TreatmentId treatmentId,
                                                  final MoneyValue newPrice) {
        return new TreatmentPriceUpdatedEvent(
            EventId.generate(),
            TimestampValue.now(),
            catalogId,
            treatmentId,
            newPrice
        );
    }
} 