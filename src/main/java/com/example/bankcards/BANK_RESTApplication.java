package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class BANK_RESTApplication {
    public static void main(String[] args) {
        SpringApplication.run(BANK_RESTApplication.class, args);
    }
}


