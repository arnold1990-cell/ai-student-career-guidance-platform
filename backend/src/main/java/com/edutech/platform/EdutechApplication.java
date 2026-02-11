package com.edutech.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EdutechApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdutechApplication.class, args);
    }
}
