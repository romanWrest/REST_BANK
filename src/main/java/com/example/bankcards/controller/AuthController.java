package com.example.bankcards.controller;

import com.example.bankcards.dto.Jwt.JwtAuthenticationDto;
import com.example.bankcards.dto.Jwt.RefreshTokenDto;
import com.example.bankcards.dto.User.UserDTO;
import com.example.bankcards.dto.User.UserRegisterDTO;
import com.example.bankcards.dto.User.UserSignInDTO;
import com.example.bankcards.security.jwt.JwtService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication API", description = "API для аутентификации и регистрации пользователей")
public class AuthController {
    private static final Logger log = LogManager.getLogger(JwtService.class);

    private final UserService userService;

    @PostMapping("/sign-in")
    @Operation(summary = "Вход в систему", description = "Аутентифицирует пользователя и возвращает JWT-токен.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация, токен возвращён"),
            @ApiResponse(responseCode = "400", description = "Неверные данные в запросе"),
            @ApiResponse(responseCode = "401", description = "Ошибка аутентификации")
    })
    public ResponseEntity<JwtAuthenticationDto> signIn(@Valid @RequestBody UserSignInDTO userSignInDTO) throws AuthenticationException {
            JwtAuthenticationDto jwtAuthenticationDto = userService.signIn(userSignInDTO);
            log.info("Generated JWT token");
            return ResponseEntity.ok(jwtAuthenticationDto);

    }

    @PostMapping("/registration")
    @Operation(summary = "Регистрация пользователя", description = "Регистрирует нового пользователя и возвращает данные пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Неверные данные в запросе"),
            @ApiResponse(responseCode = "409", description = "Пользователь с такими данными уже существует")
    })
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        UserDTO userDTO = userService.registerUser(userRegisterDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Обновление токена", description = "Обновляет JWT-токен на основе предоставленного refresh-токена.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен успешно обновлён"),
            @ApiResponse(responseCode = "400", description = "Неверный refresh-токен"),
            @ApiResponse(responseCode = "401", description = "Ошибка авторизации")
    })
    public JwtAuthenticationDto refresh(@RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
        return userService.refreshToken(refreshTokenDto);
    }
}