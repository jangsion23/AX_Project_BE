package com.knuaf.chickenstock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ChickenstockApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChickenstockApplication.class, args);
	}

}
