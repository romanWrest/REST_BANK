package com.example.bankcards.controller;

import com.example.bankcards.dto.Jwt.JwtAuthenticationDto;
import com.example.bankcards.dto.Jwt.RefreshTokenDto;
import com.example.bankcards.dto.User.UserRegisterDTO;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/sing-in")
    public ResponseEntity<JwtAuthenticationDto> singIn(@RequestBody UserRegisterDTO userRegisterDTO) {
        try {
            JwtAuthenticationDto jwtAuthenticationDto = userService.singIn(userRegisterDTO);
            return ResponseEntity.ok(jwtAuthenticationDto);
        } catch (AuthenticationException e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        } catch (javax.naming.AuthenticationException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/refresh")
    public JwtAuthenticationDto refresh(@RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
        return userService.refreshToken(refreshTokenDto);
    }
}