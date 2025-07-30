package com.example.bankcards.service;

import com.example.bankcards.dto.Jwt.JwtAuthenticationDto;
import com.example.bankcards.dto.Jwt.RefreshTokenDto;
import com.example.bankcards.dto.User.UserDTO;
import com.example.bankcards.dto.User.UserRegisterDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.RoleUsers;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    JwtAuthenticationDto signIn(UserRegisterDTO userRegisterDTO) throws AuthenticationException {
        User user = findByUserRegister(userRegisterDTO);
        return jwtService.generateAutoToken(user.getEmail());
    }

    JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        String refreshToken = refreshTokenDto.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
                User user = findByEmail(jwtService.getEmailFromToken(refreshToken));
                return jwtService.refreshBaseToken(user.getEmail(), refreshToken);
        }
        throw new AuthenticationException("Invalid refresh token");
    }

    @Transactional
    public UserDTO registerUser(UserRegisterDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Почта уже существует");
        }
        if (userRepository.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Номер телефона уже существует");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(RoleUsers.ROLE_USER);
        user = userRepository.save(user);
        return toUserDTO(user);
    }


    public UserDTO getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // только для админа и юзера
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !user.getEmail().equals(auth.getName())) {
            throw new AccessDeniedException("Access denied");
        }

        return toUserDTO(user);
    }

    public Page<UserDTO> getAllUsers(Pageable pageable) {
        // для админа
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new AccessDeniedException("Access denied");
        }

        return userRepository.findAll(pageable).map(this::toUserDTO);
    }

    private UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        return dto;
    }

    private User findByUserRegister(UserRegisterDTO dto) throws AuthenticationException {
        Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                return user;
            }
        }
        throw new AuthenticationException("Email or password is not correst");
    }

    private User findByEmail(String email) throws Exception {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new Exception(String.format("User with email %s not found", email)));
    }
}
