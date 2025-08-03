package com.example.bankcards.service;

import com.example.bankcards.dto.Card.CardDTO;
import com.example.bankcards.dto.Jwt.JwtAuthenticationDto;
import com.example.bankcards.dto.Jwt.RefreshTokenDto;
import com.example.bankcards.dto.User.UserDTO;
import com.example.bankcards.dto.User.UserRegisterDTO;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.RoleUsers;
import com.example.bankcards.exception.DuplicateResourceException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.mappers.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final CardMapper cardMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, UserMapper userMapper, CardMapper cardMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.cardMapper = cardMapper;
        logger.info("Инициализация сервиса UserService");
    }

    public JwtAuthenticationDto signIn(UserRegisterDTO userRegisterDTO) throws AuthenticationException {
        logger.info("Начало аутентификации пользователя с email: {}", userRegisterDTO.getEmail());
        UserEntity userEntity = findByUserRegister(userRegisterDTO);
        logger.info("Аутентификация успешна для email: {}", userRegisterDTO.getEmail());
        JwtAuthenticationDto token = jwtService.generateAutoToken(userEntity.getEmail());
        logger.debug("Сгенерирован токен для email: {}", userEntity.getEmail());
        return token;
    }

    public JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        logger.info("Запрос на обновление токена с refresh token: {}", refreshTokenDto.getRefreshToken());
        String refreshToken = refreshTokenDto.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            logger.debug("Валидация refresh token успешна");
            UserEntity userEntity = findByEmail(jwtService.getEmailFromToken(refreshToken));
            logger.info("Токен успешно обновлен для email: {}", userEntity.getEmail());
            return jwtService.refreshBaseToken(userEntity.getEmail(), refreshToken);
        }
        logger.error("Недействительный refresh token: {}", refreshToken);
        throw new AuthenticationException("Недействительный refresh token");
    }

    @Transactional
    public UserDTO registerUser(UserRegisterDTO dto) {
        logger.info("Начало регистрации пользователя с email: {}", dto.getEmail());
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.warn("Попытка регистрации с существующим email: {}", dto.getEmail());
            throw new DuplicateResourceException("Почта или номер телефона уже существует");
        }

        logger.debug("Создание сущности пользователя для email: {}", dto.getEmail());
        UserEntity userEntity = userMapper.toEntity(dto, passwordEncoder);
        userEntity.setRole(RoleUsers.ROLE_USER);
        userEntity = userRepository.save(userEntity);
        logger.info("Пользователь успешно зарегистрирован, email: {}, ID: {}", dto.getEmail(), userEntity.getId());
        return userMapper.toUserDTO(userEntity);
    }

    @Transactional(readOnly = true)
    public UserDTO getUser(Long id) {
        logger.info("Запрос пользователя с ID: {}", id);
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", id);
                    return new ResourceNotFoundException("Пользователь не найден");
                });

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Проверка прав доступа для пользователя: {}", auth.getName());
        if (!isAdmin(auth) && !userEntity.getEmail().equals(auth.getName())) {
            logger.warn("Отказано в доступе для пользователя: {} при запросе данных пользователя с ID: {}", auth.getName(), id);
            throw new AccessDeniedException("Доступ запрещен");
        }

        logger.info("Пользователь с ID {} успешно возвращен", id);
        return userMapper.toUserDTO(userEntity);
    }

    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        logger.info("Запрос данных текущего пользователя с email: {}", email);
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Пользователь с email {} не найден", email);
                    return new ResourceNotFoundException("Пользователь не найден");
                });
        logger.info("Данные текущего пользователя с email {} успешно возвращены", email);
        return userMapper.toUserDTO(userEntity);
    }

    public List<CardDTO> getUserCards(Long id) {
        logger.info("Запрос карт пользователя с ID: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", id);
                    return new ResourceNotFoundException("Пользователь не найден");
                });

        logger.debug("Проверка прав доступа для пользователя: {}", auth.getName());
        if (!isAdmin(auth) && !userEntity.getEmail().equals(auth.getName())) {
            logger.warn("Отказано в доступе к картам пользователя ID {} для: {}", id, auth.getName());
            throw new AccessDeniedException("Доступ запрещен");
        }

        logger.info("Карты пользователя с ID {} успешно возвращены, количество карт: {}", id, userEntity.getCardEntities().size());
        return userEntity.getCardEntities().stream()
                .map(cardMapper::toCardDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        logger.info("Запрос списка всех пользователей, страница: {}, размер страницы: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<UserDTO> users = userRepository.findAll(pageable).map(userMapper::toUserDTO);
        logger.info("Возвращен список пользователей, общее количество: {}, страница: {}", users.getTotalElements(), pageable.getPageNumber());
        return users;
    }

    private UserEntity findByUserRegister(UserRegisterDTO dto) throws AuthenticationException {
        logger.debug("Поиск пользователя по email: {}", dto.getEmail());
        Optional<UserEntity> optionalUser = userRepository.findByEmail(dto.getEmail());
        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();
            if (passwordEncoder.matches(dto.getPassword(), userEntity.getPassword())) {
                logger.debug("Пользователь найден и пароль совпадает, email: {}", dto.getEmail());
                return userEntity;
            }
            logger.error("Неверный пароль для email: {}", dto.getEmail());
        } else {
            logger.error("Пользователь с email {} не найден", dto.getEmail());
        }
        throw new AuthenticationException("Неверный email или пароль");
    }

    private UserEntity findByEmail(String email) throws Exception {
        logger.debug("Поиск пользователя по email: {}", email);
        return userRepository.findByEmail(email).orElseThrow(() -> {
            logger.error("Пользователь с email {} не найден", email);
            return new Exception(String.format("Пользователь с email %s не найден", email));
        });
    }
}