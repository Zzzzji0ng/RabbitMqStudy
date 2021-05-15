package com.nd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.nd.**")
public class ConfirmDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfirmDemoApplication.class, args);
    }
}
