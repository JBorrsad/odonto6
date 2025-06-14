package odoonto.domain.model.catalog.aggregates;

import org.jmolecules.ddd.annotation.AggregateRoot;

import odoonto.domain.model.catalog.valueobjects.TreatmentCatalogId;
import odoonto.domain.model.catalog.valueobjects.TreatmentId;
import odoonto.domain.model.catalog.valueobjects.TreatmentType;
import odoonto.domain.model.shared.valueobjects.MoneyValue;
import odoonto.domain.exceptions.DomainException;

import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@AggregateRoot
public class TreatmentCatalogAggregate {
    
    private final TreatmentCatalogId catalogId;
    private final Map<TreatmentId, TreatmentNode> treatments;
    private String catalogName;
    private String catalogVersion;
    private boolean isActive;

    public TreatmentCatalogAggregate(final String catalogName,
                                   final String catalogVersion) {
        validateConstructorParameters(catalogName, catalogVersion);
        
        this.catalogId = TreatmentCatalogId.generate();
        this.catalogName = catalogName;
        this.catalogVersion = catalogVersion;
        this.treatments = new HashMap<>();
        this.isActive = true;
    }

    private TreatmentCatalogAggregate(final TreatmentCatalogId catalogId,
                                     final String catalogName,
                                     final String catalogVersion,
                                     final Map<TreatmentId, TreatmentNode> treatments,
                                     final boolean isActive) {
        this.catalogId = catalogId;
        this.catalogName = catalogName;
        this.catalogVersion = catalogVersion;
        this.treatments = new HashMap<>(treatments);
        this.isActive = isActive;
    }

    public static TreatmentCatalogAggregate reconstituteFromPersistence(
            final TreatmentCatalogId catalogId,
            final String catalogName,
            final String catalogVersion,
            final Map<TreatmentId, TreatmentNode> treatments,
            final boolean isActive) {
        
        validateReconstitutionParameters(catalogId, catalogName, catalogVersion);
        
        return new TreatmentCatalogAggregate(catalogId, catalogName, catalogVersion,
                                           treatments != null ? treatments : new HashMap<>(),
                                           isActive);
    }

    public TreatmentId addRootTreatment(final TreatmentType treatmentType,
                                      final String treatmentName,
                                      final MoneyValue price) {
        validateTreatmentParameters(treatmentType, treatmentName, price);
        
        final TreatmentId treatmentId = TreatmentId.generate();
        final TreatmentNode treatmentNode = new TreatmentNode(
            treatmentId, treatmentType, treatmentName, price, null);
        
        treatments.put(treatmentId, treatmentNode);
        return treatmentId;
    }

    public TreatmentId addSubTreatment(final TreatmentId parentId,
                                     final TreatmentType treatmentType,
                                     final String treatmentName,
                                     final MoneyValue price) {
        validateTreatmentParameters(treatmentType, treatmentName, price);
        validateParentExists(parentId);
        
        final TreatmentId treatmentId = TreatmentId.generate();
        final TreatmentNode treatmentNode = new TreatmentNode(
            treatmentId, treatmentType, treatmentName, price, parentId);
        
        treatments.put(treatmentId, treatmentNode);
        treatments.get(parentId).addChild(treatmentId);
        return treatmentId;
    }

    public void updateTreatmentPrice(final TreatmentId treatmentId,
                                   final MoneyValue newPrice) {
        validatePriceUpdateParameters(treatmentId, newPrice);
        
        final TreatmentNode treatment = treatments.get(treatmentId);
        treatment.updatePrice(newPrice);
    }

    public void updateTreatmentName(final TreatmentId treatmentId,
                                  final String newName) {
        validateNameUpdateParameters(treatmentId, newName);
        
        final TreatmentNode treatment = treatments.get(treatmentId);
        treatment.updateName(newName);
    }

    public void deactivateTreatment(final TreatmentId treatmentId) {
        validateTreatmentExists(treatmentId);
        
        final TreatmentNode treatment = treatments.get(treatmentId);
        treatment.deactivate();
        
        deactivateChildrenRecursively(treatmentId);
    }

    public void activateTreatment(final TreatmentId treatmentId) {
        validateTreatmentExists(treatmentId);
        
        final TreatmentNode treatment = treatments.get(treatmentId);
        treatment.activate();
    }

    public Optional<MoneyValue> getTreatmentPrice(final TreatmentId treatmentId) {
        final TreatmentNode treatment = treatments.get(treatmentId);
        return treatment != null ? Optional.of(treatment.getPrice()) : Optional.empty();
    }

    public List<TreatmentId> getRootTreatments() {
        return treatments.values().stream()
                .filter(TreatmentNode::isRoot)
                .filter(TreatmentNode::isActive)
                .map(TreatmentNode::getId)
                .toList();
    }

    public List<TreatmentId> getChildTreatments(final TreatmentId parentId) {
        validateTreatmentExists(parentId);
        
        final TreatmentNode parent = treatments.get(parentId);
        return parent.getChildren().stream()
                .map(treatments::get)
                .filter(TreatmentNode::isActive)
                .map(TreatmentNode::getId)
                .toList();
    }

    public List<TreatmentId> getTreatmentsByType(final TreatmentType treatmentType) {
        if (treatmentType == null) {
            throw new DomainException("Treatment type cannot be null");
        }
        
        return treatments.values().stream()
                .filter(node -> node.getType().equals(treatmentType))
                .filter(TreatmentNode::isActive)
                .map(TreatmentNode::getId)
                .toList();
    }

    public boolean treatmentExists(final TreatmentId treatmentId) {
        return treatments.containsKey(treatmentId);
    }

    public boolean isTreatmentActive(final TreatmentId treatmentId) {
        final TreatmentNode treatment = treatments.get(treatmentId);
        return treatment != null && treatment.isActive();
    }

    private void deactivateChildrenRecursively(final TreatmentId parentId) {
        final TreatmentNode parent = treatments.get(parentId);
        for (final TreatmentId childId : parent.getChildren()) {
            final TreatmentNode child = treatments.get(childId);
            child.deactivate();
            deactivateChildrenRecursively(childId);
        }
    }

    private void validateParentExists(final TreatmentId parentId) {
        if (parentId == null) {
            throw new DomainException("Parent treatment ID cannot be null");
        }
        if (!treatments.containsKey(parentId)) {
            throw new DomainException("Parent treatment does not exist");
        }
    }

    private void validateTreatmentExists(final TreatmentId treatmentId) {
        if (treatmentId == null) {
            throw new DomainException("Treatment ID cannot be null");
        }
        if (!treatments.containsKey(treatmentId)) {
            throw new DomainException("Treatment does not exist");
        }
    }

    private static void validateConstructorParameters(final String catalogName,
                                                     final String catalogVersion) {
        if (catalogName == null || catalogName.trim().isEmpty()) {
            throw new DomainException("Catalog name cannot be null or empty");
        }
        if (catalogVersion == null || catalogVersion.trim().isEmpty()) {
            throw new DomainException("Catalog version cannot be null or empty");
        }
    }

    private static void validateReconstitutionParameters(final TreatmentCatalogId catalogId,
                                                        final String catalogName,
                                                        final String catalogVersion) {
        if (catalogId == null) {
            throw new DomainException("Catalog ID cannot be null");
        }
        validateConstructorParameters(catalogName, catalogVersion);
    }

    private static void validateTreatmentParameters(final TreatmentType treatmentType,
                                                   final String treatmentName,
                                                   final MoneyValue price) {
        if (treatmentType == null) {
            throw new DomainException("Treatment type cannot be null");
        }
        if (treatmentName == null || treatmentName.trim().isEmpty()) {
            throw new DomainException("Treatment name cannot be null or empty");
        }
        if (price == null) {
            throw new DomainException("Treatment price cannot be null");
        }
    }

    private void validatePriceUpdateParameters(final TreatmentId treatmentId,
                                             final MoneyValue newPrice) {
        validateTreatmentExists(treatmentId);
        if (newPrice == null) {
            throw new DomainException("New price cannot be null");
        }
    }

    private void validateNameUpdateParameters(final TreatmentId treatmentId,
                                            final String newName) {
        validateTreatmentExists(treatmentId);
        if (newName == null || newName.trim().isEmpty()) {
            throw new DomainException("New name cannot be null or empty");
        }
    }

    public TreatmentCatalogId getCatalogId() {
        return catalogId;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public String getCatalogVersion() {
        return catalogVersion;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final TreatmentCatalogAggregate that = (TreatmentCatalogAggregate) other;
        return Objects.equals(catalogId, that.catalogId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(catalogId);
    }

    private static class TreatmentNode {
        private final TreatmentId id;
        private final TreatmentType type;
        private String name;
        private MoneyValue price;
        private final TreatmentId parentId;
        private final List<TreatmentId> children;
        private boolean active;

        TreatmentNode(final TreatmentId id,
                     final TreatmentType type,
                     final String name,
                     final MoneyValue price,
                     final TreatmentId parentId) {
            this.id = id;
            this.type = type;
            this.name = name;
            this.price = price;
            this.parentId = parentId;
            this.children = new ArrayList<>();
            this.active = true;
        }

        void addChild(final TreatmentId childId) {
            if (!children.contains(childId)) {
                children.add(childId);
            }
        }

        void updatePrice(final MoneyValue newPrice) {
            this.price = newPrice;
        }

        void updateName(final String newName) {
            this.name = newName;
        }

        void activate() {
            this.active = true;
        }

        void deactivate() {
            this.active = false;
        }

        boolean isRoot() {
            return parentId == null;
        }

        TreatmentId getId() {
            return id;
        }

        TreatmentType getType() {
            return type;
        }

        MoneyValue getPrice() {
            return price;
        }

        List<TreatmentId> getChildren() {
            return Collections.unmodifiableList(children);
        }

        boolean isActive() {
            return active;
        }
    }
} 