package odoonto.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.core.importer.ImportOption;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "odoonto", importOptions = ImportOption.DoNotIncludeTests.class)
public class UbicationTest {

    @ArchTest
    static final ArchRule controllers_should_be_in_controller_rest = 
            classes()
                .that().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("..controller.rest..");

    @ArchTest
    static final ArchRule use_cases_should_be_in_application_service = 
            classes()
                .that().haveSimpleNameEndingWith("UseCase")
                .should().resideInAPackage("..application.service..");

    @ArchTest
    static final ArchRule dtos_should_be_in_application_dto = 
            classes()
                .that().haveSimpleNameEndingWith("Dto")
                .should().resideInAPackage("..application.dto..");

    @ArchTest
    static final ArchRule repository_interfaces_should_be_in_domain_repository = 
            classes()
                .that().resideInAPackage("..domain.repository..")
                .should().beInterfaces();

    @ArchTest
    static final ArchRule repository_implementations_should_be_in_infrastructure_persistence = 
            classes()
                .that().haveSimpleNameEndingWith("Repository")
                .and().areNotInterfaces()
                .should().resideInAPackage("..infrastructure.persistence..");

    @ArchTest
    static final ArchRule persistence_entities_should_be_in_infrastructure_persistence = 
            classes()
                .that().areAnnotatedWith("jakarta.persistence.Entity")
                .should().resideInAPackage("..infrastructure.persistence..");

    @ArchTest
    static final ArchRule domain_entities_should_be_in_domain_model = 
            classes()
                .that().haveSimpleNameEndingWith("Entity")
                .or().haveSimpleNameEndingWith("Aggregate")
                .and().resideInAPackage("..domain..")
                .should().resideInAPackage("..domain.model..");

    @ArchTest
    static final ArchRule value_objects_should_be_in_domain_model_valueobject = 
            classes()
                .that().haveSimpleNameEndingWith("Value")
                .or().haveSimpleNameEndingWith("VO")
                .should().resideInAPackage("..domain.model.valueobject..");

    @ArchTest
    static final ArchRule external_adapters_should_be_in_infrastructure_external = 
            classes()
                .that().haveSimpleNameContaining("Client")
                .or().haveSimpleNameContaining("Gateway")
                .or().haveSimpleNameContaining("Adapter")
                .should().resideInAPackage("..infrastructure.external..");

    @ArchTest
    static final ArchRule configurations_should_be_in_configuration = 
            classes()
                .that().areAnnotatedWith("org.springframework.context.annotation.Configuration")
                .should().resideInAPackage("..configuration..");

    @ArchTest
    static final ArchRule domain_services_should_be_in_domain_service = 
            classes()
                .that().haveSimpleNameEndingWith("DomainService")
                .should().resideInAPackage("..domain.service..");

    @ArchTest
    static final ArchRule domain_events_should_be_in_domain_event = 
            classes()
                .that().haveSimpleNameEndingWith("Event")
                .or().haveSimpleNameEndingWith("DomainEvent")
                .and().resideInAPackage("..domain..")
                .should().resideInAPackage("..domain.event..");

    @ArchTest
    static final ArchRule domain_exceptions_should_be_in_domain_exception = 
            classes()
                .that().haveSimpleNameEndingWith("Exception")
                .and().resideInAPackage("..domain..")
                .should().resideInAPackage("..domain.exception..");

    @ArchTest
    static final ArchRule mappers_should_be_in_appropriate_mapper_package = 
            classes()
                .that().haveSimpleNameEndingWith("Mapper")
                .should().resideInAnyPackage("..application.mapper..", "..infrastructure.mapper..");

    @ArchTest
    static final ArchRule command_handlers_should_be_in_application_command = 
            classes()
                .that().haveSimpleNameEndingWith("CommandHandler")
                .should().resideInAPackage("..application.command..");

    @ArchTest
    static final ArchRule query_handlers_should_be_in_application_query = 
            classes()
                .that().haveSimpleNameEndingWith("QueryHandler")
                .should().resideInAPackage("..application.query..");
} 