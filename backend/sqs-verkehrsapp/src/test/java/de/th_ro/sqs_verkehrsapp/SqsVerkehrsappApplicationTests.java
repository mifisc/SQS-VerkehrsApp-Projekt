package de.th_ro.sqs_verkehrsapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SqsVerkehrsappApplicationTests {

	@Test
	void contextLoads() {
	}

}
