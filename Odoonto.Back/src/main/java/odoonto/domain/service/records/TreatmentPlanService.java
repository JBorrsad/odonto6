package odoonto.domain.service.records;

import org.jmolecules.ddd.annotation.Service;

import odoonto.domain.model.records.entities.TreatmentEntity;
import odoonto.domain.model.records.valueobjects.TreatmentType;
import odoonto.domain.model.shared.valueobjects.MoneyValue;
import odoonto.domain.model.shared.valueobjects.DurationValue;
import odoonto.domain.repository.catalog.TreatmentCatalogRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TreatmentPlanService {
    private final TreatmentCatalogRepository catalogRepository;

    public TreatmentPlanService(final TreatmentCatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public MoneyValue calculateTotalCost(final List<TreatmentEntity> treatments) {
        final BigDecimal totalAmount = treatments.stream()
            .map(TreatmentEntity::getCost)
            .map(MoneyValue::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MoneyValue(totalAmount, "USD");
    }

    public DurationValue calculateTotalDuration(final List<TreatmentEntity> treatments) {
        final int totalMinutes = treatments.stream()
            .mapToInt(treatment -> getTreatmentDuration(treatment.getTreatmentType()))
            .sum();

        return new DurationValue(totalMinutes);
    }

    public boolean isEmergencyTreatment(final TreatmentType treatmentType) {
        return treatmentType == TreatmentType.EXTRACTION ||
               treatmentType == TreatmentType.ROOT_CANAL;
    }

    public boolean requiresMultipleSessions(final TreatmentType treatmentType) {
        return treatmentType == TreatmentType.ROOT_CANAL ||
               treatmentType == TreatmentType.ORTHODONTICS ||
               treatmentType == TreatmentType.PERIODONTAL;
    }

    private int getTreatmentDuration(final TreatmentType treatmentType) {
        return switch (treatmentType) {
            case CONSULTATION -> 30;
            case CLEANING -> 60;
            case FILLING -> 45;
            case EXTRACTION -> 30;
            case ROOT_CANAL -> 90;
            case CROWN -> 120;
            case BRIDGE -> 150;
            case IMPLANT -> 180;
            case ORTHODONTICS -> 60;
            case PERIODONTAL -> 75;
        };
    }
} 