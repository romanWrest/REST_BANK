package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final User user;
    private final PasswordEncoder passwordEncoder;


}
