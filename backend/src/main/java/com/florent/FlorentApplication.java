package com.florent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FlorentApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlorentApplication.class, args);
    }
}
