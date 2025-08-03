package com.example.bankcards;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
/*
@ComponentScan({"com.example.bankcards.controller."})
*/
@OpenAPIDefinition(
        info = @Info(
                title = "Bank Card API",
                version = "1.0",
                description = "API for managing bank cards",
                contact = @Contact(name = "Junior Dev Team", email = "dev@example.com")
        )
)

public class BANK_RESTApplication {
    public static void main(String[] args) {
        SpringApplication.run(BANK_RESTApplication.class, args);
    }
}


