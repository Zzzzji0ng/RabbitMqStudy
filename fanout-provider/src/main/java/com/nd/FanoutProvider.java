package com.nd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.nd.**")
public class FanoutProvider {
    public static void main(String[] args) {
        SpringApplication.run(FanoutProvider.class, args);
    }

}
