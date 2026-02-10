package com.edurite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EduRiteApplication {
    public static void main(String[] args) {
        SpringApplication.run(EduRiteApplication.class, args);
    }
}
