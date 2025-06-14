package odoonto.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.core.importer.ImportOption;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "odoonto", importOptions = ImportOption.DoNotIncludeTests.class)
public class AcopplementTest {

    @ArchTest
    static final ArchRule domain_classes_cannot_import_spring = 
            noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..");

    @ArchTest
    static final ArchRule domain_classes_cannot_have_spring_annotations = 
            noClasses()
                .that().resideInAPackage("..domain..")
                .should().beAnnotatedWith("org.springframework.stereotype.Component")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Service")
                .orShould().beAnnotatedWith("jakarta.persistence.Entity");

    @ArchTest
    static final ArchRule domain_entities_should_only_throw_domain_exceptions = 
            noClasses()
                .that().resideInAPackage("..domain.model..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("java.lang.Exception", "java.lang.RuntimeException")
                .andShould().beAssignableTo("java.lang.Throwable");

    @ArchTest
    static final ArchRule use_cases_should_not_depend_on_http_classes = 
            noClasses()
                .that().resideInAPackage("..application.service..")
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                    "javax.servlet..",
                    "jakarta.servlet..",
                    "org.springframework.http..",
                    "org.springframework.web.."
                );

    @ArchTest
    static final ArchRule only_controllers_can_return_http_classes = 
            noMethods()
                .that().areDeclaredInClassesThat()
                .resideOutsideOfPackage("..controller.rest..")
                .should().haveRawReturnType("org.springframework.http.ResponseEntity")
                .orShould().haveRawReturnType("org.springframework.http.HttpEntity");

    @ArchTest
    static final ArchRule no_field_injection_anywhere = 
            noFields()
                .that().areAnnotatedWith("org.springframework.beans.factory.annotation.Autowired")
                .should().beDeclaredInClassesThat()
                .resideInAnyPackage("odoonto..");

    @ArchTest
    static final ArchRule domain_should_not_access_infrastructure_directly = 
            noClasses()
                .that().resideInAPackage("..domain..")
                .should().accessClassesThat()
                .resideInAPackage("..infrastructure..");

    @ArchTest
    static final ArchRule application_should_not_access_infrastructure_directly = 
            noClasses()
                .that().resideInAPackage("..application..")
                .should().accessClassesThat()
                .resideInAPackage("..infrastructure..");

    @ArchTest
    static final ArchRule use_cases_should_be_transactional_boundaries = 
            classes()
                .that().resideInAPackage("..application.service..")
                .and().haveSimpleNameEndingWith("UseCase")
                .should().beAnnotatedWith("org.springframework.transaction.annotation.Transactional")
                .because("UseCase should represent transactional boundaries for aggregates");

    @ArchTest
    static final ArchRule value_objects_should_override_equals_and_hashcode = 
            fields()
                .that().areDeclaredInClassesThat()
                .resideInAPackage("..domain.model..valueobject..")
                .should().beFinal()
                .because("Value objects should be immutable and override equals/hashCode");

    @ArchTest
    static final ArchRule entities_should_have_identity_field = 
            classes()
                .that().resideInAPackage("..domain.model..")
                .and().haveSimpleNameEndingWith("Entity")
                .should().bePublic()
                .because("Entities should have an identity field (manually verify 'id' field exists)");

    @ArchTest
    static final ArchRule repositories_should_work_only_with_aggregate_roots = 
            noMethods()
                .that().areDeclaredInClassesThat()
                .resideInAPackage("..domain.repository..")
                .should().haveNameMatching(".*Entity.*")
                .because("Repositories should only return Aggregate roots, not individual entities");

    @ArchTest
    static final ArchRule command_handlers_should_modify_state = 
            classes()
                .that().resideInAPackage("..application.command..")
                .should().beAnnotatedWith("org.springframework.transaction.annotation.Transactional")
                .because("Command handlers modify state and should be transactional");

    @ArchTest
    static final ArchRule query_handlers_should_not_modify_state = 
            noClasses()
                .that().resideInAPackage("..application.query..")
                .should().beAnnotatedWith("org.springframework.transaction.annotation.Transactional")
                .because("Query handlers should not modify state");

    @ArchTest
    static final ArchRule aggregates_should_not_reference_other_aggregates_directly = 
            noClasses()
                .that().resideInAPackage("..domain.model..")
                .and().haveSimpleNameEndingWith("Aggregate")
                .should().dependOnClassesThat()
                .resideInAPackage("..domain.model..")
                .because("Aggregates should reference other aggregates only by ID");

    @ArchTest
    static final ArchRule repositories_should_use_aggregate_id_for_queries = 
            noMethods()
                .that().areDeclaredInClassesThat()
                .resideInAPackage("..domain.repository..")
                .and().haveNameStartingWith("findBy")
                .should().haveNameMatching(".*Aggregate.*")
                .because("Repository queries should use aggregate IDs, not aggregate instances");

    @ArchTest
    static final ArchRule domain_services_should_coordinate_multiple_aggregates = 
            noClasses()
                .that().resideInAPackage("..domain.service..")
                .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure..")
                .because("Domain services should coordinate aggregates without infrastructure dependencies");
} 