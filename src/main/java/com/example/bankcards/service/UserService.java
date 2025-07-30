package com.example.bankcards.service;

import com.example.bankcards.dto.User.UserResponseDTO;
import com.example.bankcards.dto.User.UserRegisterRequestDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.RoleUsers;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.bankcards.entity.enums.RoleUsers.ROLE_ADMIN;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO registerUser(UserRegisterRequestDTO dto) {

        // Ты ходишь в бд дважды там, где можно сходить 1 раз.
        // userRepository.findByUsernameOrPhoneNumber(dto.getEmail(), dto.getPhoneNumber())
        if (userRepository.findByUsername(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Почта уже существует");
        }
       if (userRepository.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Номер телефона уже существует");
        }


        // Вот так работает билдер с аннотацией @Accessors(chain = true) (Поставлена на класс User)
        User user = new User()
                .setEmail(dto.getEmail())
                .setFullName(dto.getFullName())
                .setPhoneNumber(dto.getPhoneNumber())
                .setPassword(passwordEncoder.encode(dto.getPassword()))
                .setRole(RoleUsers.ROLE_USER);
        // Но лучше сделай маппер по аналогии с CardMapper))
        user = userRepository.save(user);

        return toUserDTO(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserResponseDTO getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // только для админа и юзера
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //  boolean isAdmin у тебя в коде встречается 4 раза. Стоит вынести на утильный класс AuthUtils
        boolean isAdmin = auth.getAuthorities().stream()
                // у тебя уже есть для этого enum. Волшебные слова в коде - лишнее.
                .anyMatch(a -> a.getAuthority().equals(ROLE_ADMIN.getAuthority()));
        if (!isAdmin && !user.getEmail().equals(auth.getName())) {
            throw new AccessDeniedException("Access denied");
        }

        return toUserDTO(user);
    }

    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        // для админа
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new AccessDeniedException("Access denied");
        }

        return userRepository.findAll(pageable).map(this::toUserDTO);
    }

    // тоже можно вынести в UserMapper по аналогии с CardMapper
    private UserResponseDTO toUserDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        return dto;
    }
}
