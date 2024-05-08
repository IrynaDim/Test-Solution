package com.dev.solution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class SolutionApplication {
    public static void main(String[] args) {
        SpringApplication.run(SolutionApplication.class, args);
    }
}
