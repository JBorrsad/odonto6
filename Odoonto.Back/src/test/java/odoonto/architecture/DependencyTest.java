package odoonto.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "odoonto", importOptions = ImportOption.DoNotIncludeTests.class)
public class DependencyTest {

    @ArchTest
    static final ArchRule no_cycles_between_packages = 
            SlicesRuleDefinition.slices()
                .matching("odoonto.(*)..")
                .should().beFreeOfCycles();

    @ArchTest
    static final ArchRule only_infrastructure_persistence_can_use_entity_annotation = 
            classes()
                .that().areAnnotatedWith("jakarta.persistence.Entity")
                .should().resideInAPackage("..infrastructure.persistence..");

    @ArchTest
    static final ArchRule only_infrastructure_persistence_can_use_jpa_repository = 
            noClasses()
                .that().resideOutsideOfPackage("..infrastructure.persistence..")
                .should().dependOnClassesThat()
                .haveNameMatching(".*JpaRepository.*");

    @ArchTest
    static final ArchRule only_controller_rest_can_use_rest_controller = 
            classes()
                .that().areAnnotatedWith("org.springframework.web.bind.annotation.RestController")
                .should().resideInAPackage("..controller.rest..");

    @ArchTest
    static final ArchRule only_application_service_can_use_service_annotation = 
            classes()
                .that().areAnnotatedWith("org.springframework.stereotype.Service")
                .should().resideInAPackage("..application.service..");

    @ArchTest
    static final ArchRule only_infrastructure_persistence_can_use_repository_annotation = 
            classes()
                .that().areAnnotatedWith("org.springframework.stereotype.Repository")
                .should().resideInAPackage("..infrastructure.persistence..");

    @ArchTest
    static final ArchRule domain_should_not_depend_on_spring_framework = 
            noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..");

    @ArchTest
    static final ArchRule domain_should_not_have_spring_annotations = 
            noClasses()
                .that().resideInAPackage("..domain..")
                .should().beAnnotatedWith("org.springframework.stereotype.Component")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Service")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Repository")
                .orShould().beAnnotatedWith("jakarta.persistence.Entity");
} 