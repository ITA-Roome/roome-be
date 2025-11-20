package com.roome.roome.be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
public class RoomeBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoomeBeApplication.class, args);
    }

}
