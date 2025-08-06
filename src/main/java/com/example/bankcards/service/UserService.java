package com.example.bankcards.service;

import com.example.bankcards.dto.Card.CardDTO;
import com.example.bankcards.dto.Jwt.JwtAuthenticationDto;
import com.example.bankcards.dto.Jwt.RefreshTokenDto;
import com.example.bankcards.dto.User.UserDTO;
import com.example.bankcards.dto.User.UserRegisterDTO;
import com.example.bankcards.dto.User.UserSignInDTO;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.RoleUsers;
import com.example.bankcards.exception.DuplicateResourceException;
import com.example.bankcards.exception.user.UserNotFoundException;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.mappers.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.bankcards.util.AuthUtils.isAdmin;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final CardMapper cardMapper;


    public JwtAuthenticationDto signIn(UserSignInDTO userSignInDTO) {
        log.info("Начало аутентификации пользователя с email: {}", userSignInDTO.getEmail());
        UserEntity userEntity = userRepository.findByEmail(userSignInDTO.getEmail())
                .orElseThrow(() -> {
                    log.error("Пользователь с email {} не найден", userSignInDTO.getEmail());
                    return new UserNotFoundException("Пользователь не найден", 404);
                });
        log.info("Аутентификация успешна для email: {}", userSignInDTO.getEmail());
        JwtAuthenticationDto token = jwtService.generateAutoToken(userEntity.getEmail());
        log.debug("Сгенерирован токен для email: {}", userEntity.getEmail());
        return token;
    }

    public JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        log.info("Запрос на обновление токена с refresh token: {}", refreshTokenDto.getRefreshToken());
        String refreshToken = refreshTokenDto.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            log.debug("Валидация refresh token успешна");
            UserEntity userEntity = findByEmail(jwtService.getEmailFromToken(refreshToken));
            log.info("Токен успешно обновлен для email: {}", userEntity.getEmail());
            return jwtService.refreshBaseToken(userEntity.getEmail(), refreshToken);
        }
        log.error("Недействительный refresh token: {}", refreshToken);
        throw new AuthenticationException("Недействительный refresh token");
    }

    @Transactional
    public UserDTO registerUser(UserRegisterDTO dto) {
        log.info("Начало регистрации пользователя с email: {}", dto.getEmail());
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            log.warn("Попытка регистрации с существующим email: {}", dto.getEmail());
            throw new DuplicateResourceException("Почта уже существует");
        }

        log.debug("Создание сущности пользователя для email: {}", dto.getEmail());
        UserEntity userEntity = userMapper.toEntity(dto, passwordEncoder);
        userEntity.setRole(RoleUsers.ROLE_USER);
        userEntity.setFullName(dto.getFullname());
        userEntity = userRepository.save(userEntity);
        log.info("Пользователь успешно зарегистрирован, email: {}, ID: {}", dto.getEmail(), userEntity.getId());
        return userMapper.toUserDTO(userEntity);
    }

    @Transactional(readOnly = true)
    public UserDTO getUser(Long id) {
        log.info("Запрос пользователя с ID: {}", id);
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден", id);
                    return new UserNotFoundException("Пользователь не найден", 404);
                });
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Проверка прав доступа для пользователя: {}", auth.getName());
        if (!isAdmin(auth) && !userEntity.getEmail().equals(auth.getName())) {
            log.warn("Отказано в доступе для пользователя: {} при запросе данных пользователя с ID: {}", auth.getName(), id);
            throw new AccessDeniedException("Доступ запрещен");
        }

        log.info("Пользователь с ID {} успешно возвращен", id);
        return userMapper.toUserDTO(userEntity);
    }

    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        log.info("Запрос данных текущего пользователя с email: {}", email);
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Пользователь с email {} не найден", email);
                    return new UserNotFoundException("Пользователь не найден", 404);
                });
        log.info("Данные текущего пользователя с email {} успешно возвращены", email);
        return userMapper.toUserDTO(userEntity);
    }

    public List<CardDTO> getUserCards(Long id) {
        log.info("Запрос карт пользователя с ID: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден", id);
                    return new UserNotFoundException("Пользователь не найден", 404);
                });

        log.debug("Проверка прав доступа для пользователя: {}", auth.getName());
        if (!isAdmin(auth) && !userEntity.getEmail().equals(auth.getName())) {
            log.warn("Отказано в доступе к картам пользователя ID {} для: {}", id, auth.getName());
            throw new AccessDeniedException("Доступ запрещен");
        }

        log.info("Карты пользователя с ID {} успешно возвращены, количество карт: {}", id, userEntity.getCardEntities().size());
        return userEntity.getCardEntities().stream()
                .map(cardMapper::toCardDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        log.info("Запрос списка всех пользователей, страница: {}, размер страницы: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<UserDTO> users = userRepository.findAll(pageable).map(userMapper::toUserDTO);
        log.info("Возвращен список пользователей, общее количество: {}, страница: {}", users.getTotalElements(), pageable.getPageNumber());
        return users;
    }

    private UserEntity findByUserRegister(UserRegisterDTO dto) throws AuthenticationException {
        log.debug("Поиск пользователя по email: {}", dto.getEmail());
        Optional<UserEntity> optionalUser = userRepository.findByEmail(dto.getEmail());
        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();
            if (passwordEncoder.matches(dto.getPassword(), userEntity.getPassword())) {
                log.debug("Пользователь найден и пароль совпадает, email: {}", dto.getEmail());
                return userEntity;
            }
            log.error("Неверный пароль для email: {}", dto.getEmail());
        } else {
            log.error("Пользователь с email {} не найден", dto.getEmail());
        }
        throw new AuthenticationException("Неверный email или пароль");
    }

    private UserEntity findByEmail(String email) {
        log.debug("Поиск пользователя по email: {}", email);
        if (email == null || email.trim().isEmpty()) {
            log.error("Email не может быть пустым или null");
            throw new IllegalArgumentException("Email не может быть пустым или null");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с email " + email + " не найден", 404));
    }
}