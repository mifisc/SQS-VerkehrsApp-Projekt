package de.th_ro.sqs_verkehrsapp.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import de.th_ro.sqs_verkehrsapp.SqsVerkehrsappApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(
        packagesOf = SqsVerkehrsappApplication.class,
        importOptions = ImportOption.DoNotIncludeTests.class
)
class ArchitectureTest {

    @ArchTest
    static final ArchRule controllersShouldBeInAdapterInPackage =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..adapter.in..");

    @ArchTest
    static final ArchRule servicesShouldBeInApplicationPackage =
            classes()
                    .that().areAnnotatedWith(Service.class)
                    .should().resideInAPackage("..application..");

    @ArchTest
    static final ArchRule domainShouldNotDependOnSpring =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("org.springframework..");

    @ArchTest
    static final ArchRule domainShouldNotDependOnAdapters =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..adapter..");

    @ArchTest
    static final ArchRule applicationShouldNotDependOnWebAdapter =
            noClasses()
                    .that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..adapter.in..");

    @ArchTest
    static final ArchRule applicationShouldNotDependOnAutobahnAdapter =
            noClasses()
                    .that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..adapter.out..");

    @ArchTest
    static final ArchRule portsShouldBeInterfaces =
            classes()
                    .that().resideInAPackage("..port..")
                    .should().beInterfaces();

    @ArchTest
    static final ArchRule controllersShouldOnlyBeAccessedBySpringOrTests =
            classes()
                    .that().resideInAPackage("..adapter.in..")
                    .should().onlyBeAccessed().byAnyPackage(
                            "..adapter.in..",
                            "org.springframework.."
                    );
}
