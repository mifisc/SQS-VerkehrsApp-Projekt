package de.th_ro.sqs_verkehrsapp;

import org.springframework.boot.SpringApplication;

public class TestSqsVerkehrsappApplication {

	public static void main(String[] args) {
		SpringApplication.from(SqsVerkehrsappApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
