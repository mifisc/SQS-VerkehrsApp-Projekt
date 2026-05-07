package de.th_ro.sqs_verkehrsapp.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

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
    static final ArchRule controllersShouldBeInWebAdapterPackage =
            classes()
                    .that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..adapter.in.web..");

    @ArchTest
    static final ArchRule dtoClassesOfWebAdapterShouldBeInDtoPackage =
            classes()
                    .that().haveSimpleNameEndingWith("Dto")
                    .and().resideInAPackage("..adapter.in.web..")
                    .should().resideInAPackage("..adapter.in.web.dto..");

    @ArchTest
    static final ArchRule servicesShouldBeInApplicationServicePackage =
            classes()
                    .that().areAnnotatedWith(Service.class)
                    .should().resideInAPackage("..application.service..");

    @ArchTest
    static final ArchRule applicationPortsShouldBeInterfaces =
            classes()
                    .that().resideInAPackage("..application.port..")
                    .should().beInterfaces();

    @ArchTest
    static final ArchRule incomingPortsShouldOnlyBeInApplicationPortInPackage =
            classes()
                    .that().haveSimpleNameEndingWith("UseCase")
                    .should().resideInAPackage("..application.port.in..")
                    .andShould().beInterfaces();

    @ArchTest
    static final ArchRule outgoingPortsShouldOnlyBeInApplicationPortOutPackage =
            classes()
                    .that().haveSimpleNameEndingWith("Port")
                    .should().resideInAPackage("..application.port.out..")
                    .andShould().beInterfaces();

    @ArchTest
    static final ArchRule domainShouldNotDependOnSpring =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("org.springframework..");

    @ArchTest
    static final ArchRule domainShouldNotDependOnApplication =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..application..");

    @ArchTest
    static final ArchRule domainShouldNotDependOnAdapters =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..adapter..");

    @ArchTest
    static final ArchRule applicationShouldNotDependOnAdapters =
            noClasses()
                    .that().resideInAPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..adapter..");

    @ArchTest
    static final ArchRule incomingAdaptersShouldNotDependOnOutgoingAdapters =
            noClasses()
                    .that().resideInAPackage("..adapter.in..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..adapter.out..");

    @ArchTest
    static final ArchRule outgoingAdaptersShouldNotDependOnIncomingAdapters =
            noClasses()
                    .that().resideInAPackage("..adapter.out..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..adapter.in..");

    @ArchTest
    static final ArchRule webAdapterShouldOnlyAccessIncomingPorts =
            noClasses()
                    .that().resideInAPackage("..adapter.in.web..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..application.port.out..");

    @ArchTest
    static final ArchRule outgoingAdaptersShouldOnlyAccessOutgoingPorts =
            noClasses()
                    .that().resideInAPackage("..adapter.out..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..application.port.in..");

    @ArchTest
    static final ArchRule configMayWireAdaptersButDomainMustStayClean =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..config..");

    @ArchTest
    static final ArchRule persistenceEntitiesShouldStayInPersistenceAdapter =
            classes()
                    .that().haveSimpleNameEndingWith("Entity")
                    .should().resideInAPackage("..adapter.out..persistence..");

    @ArchTest
    static final ArchRule repositoriesShouldStayInPersistenceAdapter =
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .should().resideInAPackage("..adapter.out..persistence..");

    @ArchTest
    static final ArchRule apiClientsAndMappersShouldStayInAutobahnAdapter =
            classes()
                    .that().haveNameMatching(".*(ApiClient|ApiMapper)")
                    .should().resideInAPackage("..adapter.out.autobahn..");
}