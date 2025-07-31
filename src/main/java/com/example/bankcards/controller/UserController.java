package com.example.bankcards.controller;

import com.example.bankcards.dto.User.UserDTO;
import com.example.bankcards.dto.User.UserRegisterDTO;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        UserDTO userDTO = userService.registerUser(userRegisterDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @GetMapping("/test")
    public String test() {
        return "Test OK";
    }
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Bank Cards API Home");
    }

    @GetMapping("/registration")
    public ResponseEntity<String> showRegistrationForm() {
        return ResponseEntity.ok("Registration endpoint for GET /api/users/registration");
    }
    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable("id") Long id) {
        return userService.getUser(id);
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
