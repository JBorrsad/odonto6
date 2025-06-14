package odoonto.domain.repository.catalog;

import org.jmolecules.ddd.annotation.Repository;

import odoonto.domain.model.catalog.aggregates.TreatmentCatalogAggregate;
import odoonto.domain.model.catalog.valueobjects.CatalogId;
import odoonto.domain.model.records.valueobjects.TreatmentId;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentCatalogRepository {
    void save(TreatmentCatalogAggregate catalog);
    Optional<TreatmentCatalogAggregate> findById(CatalogId catalogId);
    List<TreatmentCatalogAggregate> findAll();
    Optional<TreatmentCatalogAggregate> findByTreatmentId(TreatmentId treatmentId);
} 