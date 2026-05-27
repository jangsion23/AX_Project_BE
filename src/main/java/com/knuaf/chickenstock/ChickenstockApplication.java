// ChickenstockApplication.java
package com.knuaf.chickenstock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing; // 추가

@EnableJpaAuditing // JPA Auditing 활성화
@EnableScheduling
@SpringBootApplication
public class ChickenstockApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChickenstockApplication.class, args);
    }
}