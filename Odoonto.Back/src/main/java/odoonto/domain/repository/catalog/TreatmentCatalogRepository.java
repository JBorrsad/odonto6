package odoonto.domain.repository.catalog;

import odoonto.domain.model.catalog.aggregates.TreatmentCatalogAggregate;
import odoonto.domain.model.catalog.valueobjects.TreatmentType;
import odoonto.domain.model.shared.valueobjects.MoneyValue;

import java.util.List;
import java.util.Optional;

public interface TreatmentCatalogRepository {
    
    void save(TreatmentCatalogAggregate catalog);
    
    Optional<TreatmentCatalogAggregate> findById(String catalogId);
    
    Optional<TreatmentCatalogAggregate> findByCode(String treatmentCode);
    
    Optional<TreatmentCatalogAggregate> findByName(String treatmentName);
    
    List<TreatmentCatalogAggregate> findByType(TreatmentType treatmentType);
    
    List<TreatmentCatalogAggregate> findByCategory(String category);
    
    List<TreatmentCatalogAggregate> findPreventiveTreatments();
    
    List<TreatmentCatalogAggregate> findRestorativeTreatments();
    
    List<TreatmentCatalogAggregate> findEndodonticTreatments();
    
    List<TreatmentCatalogAggregate> findPeriodonticTreatments();
    
    List<TreatmentCatalogAggregate> findOrthodonticTreatments();
    
    List<TreatmentCatalogAggregate> findProstheticTreatments();
    
    List<TreatmentCatalogAggregate> findSurgicalTreatments();
    
    List<TreatmentCatalogAggregate> findPediatricTreatments();
    
    List<TreatmentCatalogAggregate> findCosmeticTreatments();
    
    List<TreatmentCatalogAggregate> findDiagnosticTreatments();
    
    List<TreatmentCatalogAggregate> findSpecialistTreatments();
    
    List<TreatmentCatalogAggregate> findBasicTreatments();
    
    List<TreatmentCatalogAggregate> findActiveTreatments();
    
    List<TreatmentCatalogAggregate> findInactiveTreatments();
    
    List<TreatmentCatalogAggregate> findTreatmentsByPriceRange(MoneyValue minPrice, 
                                                              MoneyValue maxPrice);
    
    List<TreatmentCatalogAggregate> findExpensiveTreatments(MoneyValue threshold);
    
    List<TreatmentCatalogAggregate> findAffordableTreatments(MoneyValue threshold);
    
    List<TreatmentCatalogAggregate> findTreatmentsByDuration(int minMinutes, int maxMinutes);
    
    List<TreatmentCatalogAggregate> findQuickTreatments(int maxMinutes);
    
    List<TreatmentCatalogAggregate> findLongTreatments(int minMinutes);
    
    List<TreatmentCatalogAggregate> findTreatmentsBySessionCount(int minSessions, int maxSessions);
    
    List<TreatmentCatalogAggregate> findSingleSessionTreatments();
    
    List<TreatmentCatalogAggregate> findMultiSessionTreatments();
    
    List<TreatmentCatalogAggregate> findAllTreatments();
    
    boolean existsByCode(String treatmentCode);
    
    boolean existsByName(String treatmentName);
    
    long countTreatmentsByType(TreatmentType treatmentType);
    
    long countActiveTreatments();
    
    long countSpecialistTreatments();
    
    MoneyValue getAverageTreatmentPrice();
    
    MoneyValue getMaxTreatmentPrice();
    
    MoneyValue getMinTreatmentPrice();
    
    void delete(String catalogId);
} 