package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class BANK_RESTApplication {
    public static void main(String[] args) {
        //SpringApplication.run(BANK_RESTApplication.class, args);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "12345678";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
        String p = "$2a$10$oKann/GUvPot9Sj97VZhCelzkgd9p.5prV5PSMQsalKhueAfOx8JO";

    }

}


