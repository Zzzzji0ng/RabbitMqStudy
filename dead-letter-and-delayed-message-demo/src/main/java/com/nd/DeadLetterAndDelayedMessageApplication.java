package com.nd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.nd.**")
public class DeadLetterAndDelayedMessageApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeadLetterAndDelayedMessageApplication.class, args);
    }
}
