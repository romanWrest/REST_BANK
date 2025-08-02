package com.example.bankcards.service;

import com.example.bankcards.dto.Card.CardDTO;
import com.example.bankcards.dto.Jwt.JwtAuthenticationDto;
import com.example.bankcards.dto.Jwt.RefreshTokenDto;
import com.example.bankcards.dto.User.UserDTO;
import com.example.bankcards.dto.User.UserRegisterDTO;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.mappers.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LogManager.getLogger(JwtService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final CardMapper cardMapper;

    public JwtAuthenticationDto singIn(UserRegisterDTO userRegisterDTO) throws AuthenticationException {
        log.info("Начало аутентификации пользователя с email: {}", userRegisterDTO.getEmail());
        UserEntity userEntity = findByUserRegister(userRegisterDTO);
        log.info("Аутентификация успешна для email: {}", userRegisterDTO.getEmail());
        return jwtService.generateAutoToken(userEntity.getEmail());
    }

    public JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        log.info("Запрос на обновление токена с refresh token: {}", refreshTokenDto.getRefreshToken());
        String refreshToken = refreshTokenDto.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            UserEntity userEntity = findByEmail(jwtService.getEmailFromToken(refreshToken));
            log.info("Токен успешно обновлен для email: {}", userEntity.getEmail());
            return jwtService.refreshBaseToken(userEntity.getEmail(), refreshToken);
        }
        log.error("Недействительный refresh token: {}", refreshToken);
        throw new AuthenticationException("Invalid refresh token");
    }

    @Transactional
    public UserDTO registerUser(UserRegisterDTO dto) {
        log.info("Начало регистрации пользователя с email: {}", dto.getEmail());
        if (userRepository.findByEmailOrPhoneNumber(dto.getEmail()).isPresent()) {
            log.warn("Попытка регистрации с  существующим email или номером телефона: {}", dto.getEmail());
            throw new IllegalArgumentException("Почта или номер телефона уже существует");
        }

        UserEntity userEntity = userMapper.toEntity(dto, passwordEncoder);
        userEntity = userRepository.save(userEntity);  // уменьишл бройлеркод - вынес в маппер
        log.info("Пользователь успешно зарегистрирован, email: {}, ID: {}", dto.getEmail(), userEntity.getId());
        return userMapper.toUserDTO(userEntity);
    }

    @Transactional(readOnly = true)
    public UserDTO getUser(Long id) {
        log.info("Запрос пользователя с ID: {}", id);
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден", id);
                    return new ResourceNotFoundException("User not found");
                });


        // только для админа
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAdmin(auth) && !userEntity.getEmail().equals(auth.getName())) {
            throw new AccessDeniedException("Access denied");
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toUserDTO(userEntity);
    }


    public List<CardDTO> getUserCards(Long id) {
        log.info("Запрос карт пользователя с ID: {}", id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!isAdmin(auth) && !userEntity.getEmail().equals(auth.getName())) {
            log.warn("Отказано в доступе к картам пользователя ID {} для: {}", id, auth.getName());
            throw new AccessDeniedException("Access denied");
        }

        return userEntity.getCardEntities().stream()
                .map(cardMapper::toCardDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        // для админа
        log.info("Запрос списка всех пользователей, страница: {}", pageable.getPageNumber());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAdmin(auth)) {
            log.warn("Отказано в доступе к списку пользователей для нотадмина: {}", auth.getName());
            throw new AccessDeniedException("Access denied");
        }

        Page<UserDTO> users = userRepository.findAll(pageable).map(userMapper::toUserDTO);
        log.info("Возвращен список пользователей, количество: {}", users.getTotalElements());
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
        throw new AuthenticationException("Email or password is not correst");
    }
    // сделать для админа
    private UserEntity findByEmail(String email) throws Exception {
        log.debug("Поиск пользователя по email: {}", email);
        return userRepository.findByEmail(email).orElseThrow(() -> {
                    log.error("Пользователь с email {} не найден", email);
                    return new Exception(String.format("User with email %s not found", email));
                }
        );
    }
}
