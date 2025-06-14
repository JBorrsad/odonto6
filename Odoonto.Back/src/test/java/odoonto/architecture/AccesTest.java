package odoonto.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.core.importer.ImportOption;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "odoonto", importOptions = ImportOption.DoNotIncludeTests.class)
public class AccesTest {

    @ArchTest
    static final ArchRule controllers_can_only_access_application = 
            classes()
                .that().resideInAPackage("..controller..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                    "java..",
                    "org.springframework..",
                    "jakarta..",
                    "..application..",
                    "lombok..",
                    "org.slf4j.."
                );

    @ArchTest
    static final ArchRule application_services_can_only_access_domain_and_dto = 
            classes()
                .that().resideInAPackage("..application.service..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                    "java..",
                    "org.springframework.stereotype.Service",
                    "..domain..",
                    "..application.dto..",
                    "lombok..",
                    "org.slf4j.."
                );

    @ArchTest
    static final ArchRule domain_cannot_access_any_other_layer = 
            noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                    "org.springframework..",
                    "..infrastructure..",
                    "..controller..",
                    "..application.."
                );

    @ArchTest
    static final ArchRule infrastructure_can_access_domain_repository_but_not_controller_application = 
            noClasses()
                .that().resideInAPackage("..infrastructure..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..controller..", "..application..");

    @ArchTest
    static final ArchRule dtos_cannot_depend_on_classes_outside_application = 
            noClasses()
                .that().resideInAPackage("..application.dto..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                    "..domain..",
                    "..infrastructure..",
                    "..controller.."
                );

    @ArchTest
    static final ArchRule infrastructure_adapters_should_not_access_domain_model_directly = 
            noClasses()
                .that().resideInAPackage("..infrastructure..")
                .should().dependOnClassesThat()
                .resideInAPackage("..domain.model..");
} 