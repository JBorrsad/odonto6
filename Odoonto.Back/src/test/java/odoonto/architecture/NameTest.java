package odoonto.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.core.importer.ImportOption;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "odoonto", importOptions = ImportOption.DoNotIncludeTests.class)
public class NameTest {

    @ArchTest
    static final ArchRule controllers_should_end_with_controller = 
            classes()
                .that().resideInAPackage("..controller.rest..")
                .should().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule use_cases_should_end_with_use_case = 
            classes()
                .that().resideInAPackage("..application.service..")
                .should().haveSimpleNameEndingWith("UseCase");

    @ArchTest
    static final ArchRule domain_entities_should_end_with_entity_or_aggregate = 
            classes()
                .that().resideInAPackage("..domain.model..")
                .and().areNotInterfaces()
                .and().areNotEnums()
                .should().haveSimpleNameEndingWith("Entity")
                .orShould().haveSimpleNameEndingWith("Aggregate");

    @ArchTest
    static final ArchRule repository_interfaces_should_start_with_I_or_end_with_repository = 
            classes()
                .that().resideInAPackage("..domain.repository..")
                .and().areInterfaces()
                .should().haveSimpleNameStartingWith("I")
                .orShould().haveSimpleNameEndingWith("Repository");

    @ArchTest
    static final ArchRule dtos_should_end_with_dto = 
            classes()
                .that().resideInAPackage("..application.dto..")
                .should().haveSimpleNameEndingWith("Dto");

    @ArchTest
    static final ArchRule value_objects_should_end_with_value_or_vo = 
            classes()
                .that().resideInAPackage("..domain.model.valueobject..")
                .should().haveSimpleNameEndingWith("Value")
                .orShould().haveSimpleNameEndingWith("VO");

    @ArchTest
    static final ArchRule mappers_should_end_with_mapper = 
            classes()
                .that().resideInAnyPackage("..application.mapper..", "..infrastructure.mapper..")
                .should().haveSimpleNameEndingWith("Mapper");

    @ArchTest
    static final ArchRule configurations_should_end_with_config = 
            classes()
                .that().areAnnotatedWith("org.springframework.context.annotation.Configuration")
                .should().haveSimpleNameEndingWith("Config")
                .orShould().haveSimpleNameEndingWith("Configuration");

    @ArchTest
    static final ArchRule aggregate_root_annotated_classes_should_be_in_domain_model = 
            classes()
                .that().areAnnotatedWith("org.jmolecules.ddd.annotation.AggregateRoot")
                .should().resideInAPackage("..domain.model..");

    @ArchTest
    static final ArchRule jmolecules_repository_annotated_classes_should_be_in_domain_repository = 
            classes()
                .that().areAnnotatedWith("org.jmolecules.ddd.annotation.Repository")
                .should().resideInAPackage("..domain.repository..");
} 