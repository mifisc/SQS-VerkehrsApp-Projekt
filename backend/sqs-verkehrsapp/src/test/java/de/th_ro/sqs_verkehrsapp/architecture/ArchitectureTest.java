package de.th_ro.sqs_verkehrsapp.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ArchitectureTest {

    private final JavaClasses importedClasses =
            new ClassFileImporter().importPackages("de.th_ro.sqs_verkehrsapp");

    @Test
    void archUnitSetupWorks() {
        classes()
                .should()
                .resideInAPackage("..")
                .check(importedClasses);
    }
}
