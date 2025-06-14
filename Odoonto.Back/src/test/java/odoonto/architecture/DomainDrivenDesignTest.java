package odoonto.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "odoonto", importOptions = ImportOption.DoNotIncludeTests.class)
public class DomainDrivenDesignTest {

    // =============== UBICACIÓN Y ESTRUCTURA DDD ===============

    @ArchTest
    static final ArchRule aggregate_roots_should_be_clearly_identified = 
            classes()
                .that().haveSimpleNameEndingWith("Aggregate")
                .or().haveSimpleNameEndingWith("AggregateRoot")
                .should().resideInAPackage("..domain.model..");

    @ArchTest
    static final ArchRule domain_services_should_be_in_domain_service_package = 
            classes()
                .that().haveSimpleNameEndingWith("DomainService")
                .should().resideInAPackage("..domain.service..");

    @ArchTest
    static final ArchRule domain_events_should_be_in_domain_events_package = 
            classes()
                .that().haveSimpleNameEndingWith("Event")
                .or().haveSimpleNameEndingWith("DomainEvent")
                .and().resideInAPackage("..domain..")
                .should().resideInAPackage("..domain.events..");

    @ArchTest
    static final ArchRule factories_should_be_in_model_factory_package = 
            classes()
                .that().haveSimpleNameEndingWith("Factory")
                .and().resideInAPackage("..domain..")
                .should().resideInAPackage("..domain.model..factory..");

    @ArchTest
    static final ArchRule specifications_should_be_in_domain_specification_package = 
            classes()
                .that().haveSimpleNameEndingWith("Specification")
                .or().haveSimpleNameEndingWith("Spec")
                .and().resideInAPackage("..domain..")
                .should().resideInAPackage("..domain.specification..");

    @ArchTest
    static final ArchRule domain_exceptions_should_be_in_domain_exceptions_package = 
            classes()
                .that().haveSimpleNameEndingWith("Exception")
                .and().resideInAPackage("..domain..")
                .should().resideInAPackage("..domain.exceptions..");

    // =============== LENGUAJE UBICUO Y NAMING ===============

    @ArchTest
    static final ArchRule ubiquitous_language_in_naming = 
            noClasses()
                .that().resideInAPackage("..domain..")
                .should().haveSimpleNameContaining("Data")
                .orShould().haveSimpleNameContaining("Info")
                .orShould().haveSimpleNameContaining("Object")
                .orShould().haveSimpleNameContaining("Item");

    // =============== AISLAMIENTO Y ACCESO ENTRE CAPAS ===============

    @ArchTest
    static final ArchRule domain_should_not_leak_to_other_layers = 
            noClasses()
                .that().resideOutsideOfPackage("..domain..")
                .should().accessClassesThat()
                .resideInAPackage("..domain.model..");

    @ArchTest
    static final ArchRule repositories_should_work_with_aggregates = 
            classes()
                .that().resideInAPackage("..domain.repository..")
                .and().areInterfaces()
                .should().beInterfaces();

    @ArchTest
    static final ArchRule anti_corruption_layer_for_external_systems = 
            noClasses()
                .that().resideInAPackage("..infrastructure.external..")
                .should().dependOnClassesThat()
                .resideInAPackage("..domain.model..");

    // =============== COMMAND/QUERY SEPARATION ===============

    @ArchTest
    static final ArchRule command_query_separation_in_repositories = 
            noMethods()
                .that().areDeclaredInClassesThat()
                .resideInAPackage("..domain.repository..")
                .and().haveNameStartingWith("find")
                .should().haveRawReturnType(void.class);

    // =============== INMUTABILIDAD Y ENCAPSULACIÓN ===============

    @ArchTest
    static final ArchRule value_objects_should_be_immutable_verified = 
            classes()
                .that().resideInAPackage("..domain.model..valueobject..")
                .should().haveOnlyFinalFields();

    @ArchTest
    static final ArchRule value_objects_should_be_in_valueobjects_package = 
            classes()
                .that().haveSimpleNameEndingWith("Value")
                .or().haveSimpleNameEndingWith("VO")
                .and().resideInAPackage("..domain..")
                .should().resideInAPackage("..domain.model..valueobject..");

    @ArchTest
    static final ArchRule events_must_have_required_fields = 
            fields()
                .that().areDeclaredInClassesThat()
                .resideInAPackage("..domain.events..")
                .and().areDeclaredInClassesThat().haveSimpleNameEndingWith("Event")
                .and().haveRawType("odoonto.domain.model.shared.valueobjects.EventId")
                .should().beDeclaredInClassesThat().resideInAPackage("..domain.events..");

    @ArchTest
    static final ArchRule aggregates_should_encapsulate_business_logic = 
            classes()
                .that().resideInAPackage("..domain.model..")
                .and().haveSimpleNameEndingWith("Aggregate")
                .should().bePackagePrivate()
                .orShould().bePublic();

    // =============== BOUNDED CONTEXTS ===============

    @ArchTest
    static final ArchRule bounded_contexts_should_be_isolated = 
            SlicesRuleDefinition.slices()
                .matching("odoonto.domain.model.(*)..")
                .should().notDependOnEachOther()
                .ignoreDependency("odoonto.domain.model.shared..", "..");

    @ArchTest
    static final ArchRule bounded_contexts_should_not_cross_reference = 
            noClasses()
                .that().resideInAPackage("..domain.model.patients..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..domain.model.records..", "..domain.model.scheduling..", "..domain.model.staff..", "..domain.model.catalog..");

    @ArchTest
    static final ArchRule domain_services_should_not_have_state = 
            classes()
                .that().resideInAPackage("..domain.service..")
                .should().haveOnlyFinalFields();

    @ArchTest
    static final ArchRule application_services_should_be_transaction_scripts = 
            classes()
                .that().resideInAPackage("..application.service..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                    "java..",
                    "org.springframework.stereotype..",
                    "..domain..",
                    "..application.dto..",
                    "lombok..",
                    "org.slf4j.."
                );

    // =============== JMOLECULES INTEGRATION - REGLAS CONSOLIDADAS ===============

    @ArchTest
    static final ArchRule jmolecules_annotations_should_be_used_consistently = 
            noClasses()
                .that().resideInAPackage("..domain..")
                .should().beAnnotatedWith("org.springframework.stereotype.Component")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Service")
                .orShould().beAnnotatedWith("jakarta.persistence.Entity")
                .because("Domain classes should use jMolecules annotations instead of Spring/JPA");

    @ArchTest
    static final ArchRule jmolecules_domain_concepts_should_be_properly_located = 
            classes()
                .that().areAnnotatedWith("org.jmolecules.ddd.annotation.Entity")
                .or().areAnnotatedWith("org.jmolecules.ddd.annotation.AggregateRoot")
                .or().areAnnotatedWith("org.jmolecules.ddd.annotation.ValueObject")
                .should().resideInAPackage("..domain.model..");

    @ArchTest
    static final ArchRule jmolecules_aggregate_roots_must_be_annotated = 
            classes()
                .that().resideInAPackage("..domain.model..")
                .and().haveSimpleNameEndingWith("Aggregate")
                .should().beAnnotatedWith("org.jmolecules.ddd.annotation.AggregateRoot");

    @ArchTest
    static final ArchRule jmolecules_entities_must_be_annotated = 
            classes()
                .that().resideInAPackage("..domain.model..")
                .and().haveSimpleNameEndingWith("Entity")
                .should().beAnnotatedWith("org.jmolecules.ddd.annotation.Entity");

    @ArchTest
    static final ArchRule jmolecules_value_objects_must_be_annotated = 
            classes()
                .that().resideInAPackage("..domain.model.valueobject..")
                .should().beAnnotatedWith("org.jmolecules.ddd.annotation.ValueObject");

    @ArchTest
    static final ArchRule jmolecules_repositories_must_be_annotated = 
            classes()
                .that().resideInAPackage("..domain.repository..")
                .and().areInterfaces()
                .should().beAnnotatedWith("org.jmolecules.ddd.annotation.Repository");

    @ArchTest
    static final ArchRule jmolecules_domain_services_must_be_annotated = 
            classes()
                .that().resideInAPackage("..domain.service..")
                .should().beAnnotatedWith("org.jmolecules.ddd.annotation.Service");

    @ArchTest
    static final ArchRule jmolecules_domain_events_must_be_annotated = 
            classes()
                .that().resideInAPackage("..domain.events..")
                .and().haveSimpleNameEndingWith("Event")
                .should().implement("odoonto.domain.events.shared.DomainEvent");

    // =============== NO MEZCLAR ANOTACIONES ===============

    @ArchTest
    static final ArchRule jmolecules_entities_should_not_use_jpa_annotations = 
            noClasses()
                .that().areAnnotatedWith("org.jmolecules.ddd.annotation.Entity")
                .should().beAnnotatedWith("jakarta.persistence.Entity")
                .orShould().beAnnotatedWith("jakarta.persistence.Table")
                .orShould().beAnnotatedWith("jakarta.persistence.Id");

    @ArchTest
    static final ArchRule jmolecules_repositories_should_not_use_spring_repository = 
            noClasses()
                .that().areAnnotatedWith("org.jmolecules.ddd.annotation.Repository")
                .should().beAnnotatedWith("org.springframework.stereotype.Repository");

    @ArchTest
    static final ArchRule jmolecules_domain_services_should_not_use_spring_service = 
            noClasses()
                .that().areAnnotatedWith("org.jmolecules.ddd.annotation.Service")
                .should().beAnnotatedWith("org.springframework.stereotype.Service");

    @ArchTest
    static final ArchRule domain_classes_should_prefer_jmolecules_over_infrastructure = 
            noClasses()
                .that().resideInAPackage("..domain..")
                .should().beAnnotatedWith("org.springframework.stereotype.Component")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Service")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Repository")
                .orShould().beAnnotatedWith("jakarta.persistence.Entity")
                .orShould().beAnnotatedWith("jakarta.persistence.Embeddable")
                .because("Domain classes should use jMolecules annotations instead of infrastructure annotations");

    // =============== DOMAIN POLICIES ===============

    @ArchTest
    static final ArchRule policies_should_not_depend_on_infrastructure = 
            noClasses()
                .that().resideInAPackage("..domain.model..policy..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..infrastructure..", "org.springframework..")
                .because("Domain policies should not depend on infrastructure");
} 