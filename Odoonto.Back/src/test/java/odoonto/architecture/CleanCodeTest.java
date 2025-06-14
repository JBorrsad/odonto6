package odoonto.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.core.importer.ImportOption;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "odoonto", importOptions = ImportOption.DoNotIncludeTests.class)
public class CleanCodeTest {

    @ArchTest
    static final ArchRule classes_should_have_expressive_names = 
            noClasses()
                .should().haveSimpleNameContaining("Temp")
                .orShould().haveSimpleNameContaining("Utils")
                .orShould().haveSimpleNameContaining("Helper");

    @ArchTest
    static final ArchRule no_abbreviations_in_class_names = 
            noClasses()
                .should().haveSimpleNameContaining("Ctrl")
                .orShould().haveSimpleNameContaining("Svc")
                .orShould().haveSimpleNameContaining("Res")
                .orShould().haveSimpleNameContaining("Usr");

    @ArchTest
    static final ArchRule validation_should_be_in_correct_layer = 
            noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("jakarta.validation..", "javax.validation..");

    @ArchTest
    static final ArchRule prefer_constructor_injection = 
            noFields()
                .that().areAnnotatedWith("org.springframework.beans.factory.annotation.Autowired")
                .should().beDeclaredInClassesThat()
                .resideInAnyPackage("odoonto..");

    @ArchTest
    static final ArchRule package_names_should_be_meaningful = 
            noClasses()
                .should().resideInAPackage("..misc..")
                .orShould().resideInAPackage("..common..")
                .orShould().resideInAPackage("..utils..");

    // NOTA: Las siguientes reglas se movieron a Checkstyle porque ArchUnit no puede medirlas eficientemente:
    // - Longitud de métodos (< 30 líneas)
    // - Niveles de anidación
    // - System.out.println detection
} 