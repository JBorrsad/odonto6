package odoonto.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.core.importer.ImportOption;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "odoonto", importOptions = ImportOption.DoNotIncludeTests.class)
public class SolidTest {

    // =============== SINGLE RESPONSIBILITY PRINCIPLE (SRP) ===============

    @ArchTest
    static final ArchRule controllers_should_only_coordinate = 
            noClasses()
                .that().resideInAPackage("..controller.rest..")
                .should().dependOnClassesThat()
                .resideInAPackage("..domain.model..")
                .because("Controllers should only coordinate, not contain business logic");

    @ArchTest
    static final ArchRule use_cases_should_not_contain_persistence_logic = 
            noClasses()
                .that().resideInAPackage("..application.service..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("jakarta.persistence..", "org.springframework.data..")
                .because("Use cases should not contain persistence logic");

    @ArchTest
    static final ArchRule entities_should_not_access_databases = 
            noClasses()
                .that().resideInAPackage("..domain.model..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework.data..", "jakarta.persistence..")
                .because("Domain entities should not access databases directly");

    // =============== DEPENDENCY INVERSION PRINCIPLE (DIP) ===============

    @ArchTest
    static final ArchRule use_cases_should_depend_on_interfaces = 
            classes()
                .that().resideInAPackage("..application.service..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                    "java..",
                    "org.springframework.stereotype..",
                    "..domain.repository..",
                    "..domain.model..",
                    "..application.dto..",
                    "lombok..", 
                    "org.slf4j.."
                )
                .because("Use cases should depend on interfaces, not implementations");

    @ArchTest
    static final ArchRule domain_should_not_depend_on_concrete_implementations = 
            noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure..")
                .because("Domain should not depend on concrete implementations");

    @ArchTest
    static final ArchRule application_should_depend_on_interfaces_only = 
            noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure..")
                .because("Application layer should depend on interfaces, not implementations");

    // =============== INTERFACE SEGREGATION PRINCIPLE (ISP) ===============

    @ArchTest
    static final ArchRule repository_interfaces_should_be_focused = 
            classes()
                .that().areInterfaces()
                .and().resideInAPackage("..domain.repository..")
                .should().beInterfaces()
                .because("Repository interfaces should be focused and segregated");

    // =============== OPEN/CLOSED PRINCIPLE (OCP) ===============

    @ArchTest
    static final ArchRule infrastructure_should_be_implementations = 
            classes()
                .that().resideInAPackage("..infrastructure.persistence..")
                .and().haveSimpleNameEndingWith("Repository")
                .and().areNotInterfaces()
                .should().beAssignableTo("java.lang.Object")
                .because("Infrastructure should provide implementations that can be extended");

    // NOTA: Las siguientes reglas SOLID no se pueden verificar eficientemente con ArchUnit:
    // - Métodos con menos de 30 líneas (SRP)
    // - Clases con menos de 200 líneas (SRP)  
    // - Número máximo de parámetros en métodos (SRP)
    // - Complejidad ciclomática (SRP)
    // Estas reglas deben implementarse en Checkstyle o SonarQube.
} 