package de.th_ro.sqs_verkehrsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SqsVerkehrsappApplication {

    public static void main(String[] args) {
        SpringApplication.run(SqsVerkehrsappApplication.class, args);
    }

}
