package de.th_ro.sqs_verkehrsapp;

import de.th_ro.sqs_verkehrsapp.config.AutobahnApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AutobahnApiProperties.class)
public class SqsVerkehrsappApplication {

	public static void main(String[] args) {
		SpringApplication.run(SqsVerkehrsappApplication.class, args);
	}

}
