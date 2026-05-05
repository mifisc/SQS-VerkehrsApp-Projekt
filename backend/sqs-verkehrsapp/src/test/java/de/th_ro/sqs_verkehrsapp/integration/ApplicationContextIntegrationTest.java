package de.th_ro.sqs_verkehrsapp.integration;

import de.th_ro.sqs_verkehrsapp.SqsVerkehrsappApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = SqsVerkehrsappApplication.class,
        properties = "autobahn.api.base-url=http://localhost:9999"
)
public class ApplicationContextIntegrationTest {

    @Test
    void contextLoads() {
    }
}
