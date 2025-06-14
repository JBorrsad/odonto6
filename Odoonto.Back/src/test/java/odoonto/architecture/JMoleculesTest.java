package odoonto.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.core.importer.ImportOption;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "odoonto", importOptions = ImportOption.DoNotIncludeTests.class)
public class JMoleculesTest {

    // =============== REGLAS BÁSICAS DE ANOTACIONES JMOLECULES ===============

    @ArchTest
    static final ArchRule entities_must_be_annotated_with_jmolecules = 
            classes()
                .that().resideInAPackage("..domain.model..")
                .and().haveSimpleNameEndingWith("Entity")
                .should().beAnnotatedWith("org.jmolecules.ddd.annotation.Entity");

    @ArchTest
    static final ArchRule aggregate_roots_must_be_annotated_with_jmolecules = 
            classes()
                .that().resideInAPackage("..domain.model..")
                .and().haveSimpleNameEndingWith("Aggregate")
                .should().beAnnotatedWith("org.jmolecules.ddd.annotation.AggregateRoot");

    @ArchTest
    static final ArchRule value_objects_must_be_annotated_with_jmolecules = 
            classes()
                .that().resideInAPackage("..domain.model..valueobject..")
                .should().beAnnotatedWith("org.jmolecules.ddd.annotation.ValueObject");

    @ArchTest
    static final ArchRule repositories_must_be_annotated_with_jmolecules = 
            classes()
                .that().resideInAPackage("..domain.repository..")
                .and().areInterfaces()
                .should().beAnnotatedWith("org.jmolecules.ddd.annotation.Repository");

    @ArchTest
    static final ArchRule domain_services_must_be_annotated_with_jmolecules = 
            classes()
                .that().resideInAPackage("..domain.service..")
                .should().beAnnotatedWith("org.jmolecules.ddd.annotation.Service");

    @ArchTest
    static final ArchRule domain_events_must_be_annotated_with_jmolecules = 
            classes()
                .that().resideInAPackage("..domain.events..")
                .and().haveSimpleNameEndingWith("Event")
                .should().implement("odoonto.domain.events.shared.DomainEvent");

    @ArchTest
    static final ArchRule factories_must_be_annotated_with_jmolecules = 
            classes()
                .that().resideInAPackage("..domain.model..factory..")
                .and().haveSimpleNameEndingWith("Factory")
                .should().beAnnotatedWith("org.jmolecules.ddd.annotation.Factory");

    @ArchTest
    static final ArchRule identifiers_should_be_annotated = 
            classes()
                .that().resideInAPackage("..domain.model..")
                .and().haveSimpleNameEndingWith("Id")
                .should().beAnnotatedWith("org.jmolecules.ddd.annotation.Identity");

    // =============== REGLAS DE SEPARACIÓN DE CAPAS ===============

    @ArchTest
    static final ArchRule application_services_should_not_use_jmolecules_domain_annotations = 
            noClasses()
                .that().resideInAPackage("..application.service..")
                .should().beAnnotatedWith("org.jmolecules.ddd.annotation.Entity")
                .orShould().beAnnotatedWith("org.jmolecules.ddd.annotation.AggregateRoot")
                .orShould().beAnnotatedWith("org.jmolecules.ddd.annotation.ValueObject");

    @ArchTest
    static final ArchRule infrastructure_should_not_use_jmolecules_domain_annotations = 
            noClasses()
                .that().resideInAPackage("..infrastructure..")
                .should().beAnnotatedWith("org.jmolecules.ddd.annotation.Entity")
                .orShould().beAnnotatedWith("org.jmolecules.ddd.annotation.AggregateRoot")
                .orShould().beAnnotatedWith("org.jmolecules.ddd.annotation.ValueObject")
                .orShould().beAnnotatedWith("org.jmolecules.ddd.annotation.Repository");

    // =============== REGLAS DE HERENCIA JMOLECULES ===============

    @ArchTest
    static final ArchRule domain_events_should_implement_domain_event_interface = 
            classes()
                .that().areAnnotatedWith("org.jmolecules.event.annotation.DomainEvent")
                .should().implement("org.jmolecules.event.types.DomainEvent");

    @ArchTest
    static final ArchRule aggregate_roots_should_extend_aggregate_root = 
            classes()
                .that().areAnnotatedWith("org.jmolecules.ddd.annotation.AggregateRoot")
                .should().beAssignableTo("org.jmolecules.ddd.types.AggregateRoot");

    @ArchTest
    static final ArchRule entities_should_extend_entity = 
            classes()
                .that().areAnnotatedWith("org.jmolecules.ddd.annotation.Entity")
                .should().beAssignableTo("org.jmolecules.ddd.types.Entity");

    // NOTA: Las siguientes reglas de "no mezclar anotaciones" fueron consolidadas 
    // en DomainDrivenDesignTest.java para evitar duplicidad y tener un archivo maestro DDD.
} 