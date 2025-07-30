package com.example.bankcards.controller;

import com.example.bankcards.dto.User.UserResponseDTO;
import com.example.bankcards.dto.User.UserRegisterRequestDTO;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor // так всегда красивее)
@Slf4j // За логирование всегда похвалят. Обмазывай все логами - это хорошая привычка.
// Debug - для логов, которые нужны при поиске ошибок.
// Info - обязательные события (Сохранение, удаление, изменение в бд, всякие нотификации и прочее)
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRegisterRequestDTO userRegisterDTO) {
        log.debug("Registering user: {}", userRegisterDTO);
        var userDTO = userService.registerUser(userRegisterDTO);
        log.debug("Registered user: {}", userDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUser(@PathVariable("id") Long id) {
        return userService.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable) {
        Page<UserResponseDTO> users = userService.getAllUsers(pageable);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
