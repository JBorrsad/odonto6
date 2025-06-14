package odoonto.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.GeneralCodingRules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "odoonto", importOptions = ImportOption.DoNotIncludeTests.class)
public class GoodPracticeTest {

    @ArchTest
    static final ArchRule value_objects_should_be_immutable = 
            classes()
                .that().resideInAPackage("..domain.model.valueobject..")
                .should().haveOnlyFinalFields();

    @ArchTest
    static final ArchRule no_generic_exceptions = 
            GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    @ArchTest
    static final ArchRule no_java_util_logging = 
            GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

    @ArchTest
    static final ArchRule use_cases_should_be_services = 
            classes()
                .that().resideInAPackage("..application.service..")
                .and().haveSimpleNameEndingWith("UseCase")
                .should().beAnnotatedWith("org.springframework.stereotype.Service");

    @ArchTest
    static final ArchRule entities_should_not_depend_on_persistence = 
            noClasses()
                .that().resideInAPackage("..domain.model..")
                .and().haveSimpleNameEndingWith("Entity")
                .should().dependOnClassesThat()
                .resideInAnyPackage("jakarta.persistence..", "javax.persistence..");

    @ArchTest
    static final ArchRule validation_should_occur_in_use_cases_or_dtos = 
            noClasses()
                .that().resideInAPackage("..domain.model..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("jakarta.validation..", "javax.validation..");

    @ArchTest
    static final ArchRule controllers_should_not_contain_business_logic = 
            noClasses()
                .that().resideInAPackage("..controller.rest..")
                .should().dependOnClassesThat()
                .resideInAPackage("..domain.model..");
} 