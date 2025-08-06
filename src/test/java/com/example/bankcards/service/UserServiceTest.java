package com.example.bankcards.service;

import com.example.bankcards.dto.Card.CardDTO;
import com.example.bankcards.dto.Jwt.JwtAuthenticationDto;
import com.example.bankcards.dto.Jwt.RefreshTokenDto;
import com.example.bankcards.dto.User.UserDTO;
import com.example.bankcards.dto.User.UserRegisterDTO;
import com.example.bankcards.dto.User.UserSignInDTO;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.RoleUsers;
import com.example.bankcards.exception.DuplicateResourceException;
import com.example.bankcards.exception.user.UserNotFoundException;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.mappers.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtService;
import com.example.bankcards.util.AuthUtils;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.AuthenticationException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private UserService userService;

    private UserEntity userEntity;
    private UserSignInDTO userSignInDTO;
    private UserRegisterDTO userRegisterDTO;
    private UserDTO userDTO;
    private CardEntity cardEntity;
    private CardDTO cardDTO;
    private JwtAuthenticationDto jwtAuthenticationDto;
    private RefreshTokenDto refreshTokenDto;
    private Authentication authentication;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("test@example.com");
        userEntity.setFullName("John Doe");
        userEntity.setPassword("encodedPassword");
        userEntity.setRole(RoleUsers.ROLE_USER);
        userEntity.setCardEntities(Collections.emptyList());

        userSignInDTO = new UserSignInDTO();
        userSignInDTO.setEmail("test@example.com");
        userSignInDTO.setPassword("password123");

        userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("test@example.com");
        userRegisterDTO.setFullname("John Doe");
        userRegisterDTO.setPassword("password123");

        userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setFullName("John Doe");
        userDTO.setRole(RoleUsers.ROLE_USER);

        cardEntity = new CardEntity();
        cardEntity.setId(1L);
        cardEntity.setNumber("1234567890123456");
        cardEntity.setStatus(CardStatus.ACTIVE);

        cardDTO = new CardDTO();
        cardDTO.setMaskedNumber("**** **** **** 3456");
        cardDTO.setStatus(CardStatus.ACTIVE);

        jwtAuthenticationDto = new JwtAuthenticationDto();
        jwtAuthenticationDto.setToken("mockAccessToken");
        jwtAuthenticationDto.setRefreshToken("mockRefreshToken");

        refreshTokenDto = new RefreshTokenDto();
        refreshTokenDto.setRefreshToken("mockRefreshToken");
    }

    @Test
    void testSignIn_Successful() {
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));
        when(jwtService.generateAutoToken("test@example.com")).thenReturn(jwtAuthenticationDto);

        
        JwtAuthenticationDto result = userService.signIn(userSignInDTO);

        
        assertNotNull(result);
        assertEquals("mockAccessToken", result.getToken());
        assertEquals("mockRefreshToken", result.getRefreshToken());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(jwtService, times(1)).generateAutoToken("test@example.com");
    }

    @Test
    void testSignIn_UserNotFound_ThrowsException() {
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.signIn(userSignInDTO));
        assertEquals("Пользователь не найден", exception.getMessage());
        assertEquals(404, exception.getStatus());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(jwtService, never()).generateAutoToken(any());
    }

    @Test
    void testRefreshToken_Successful() throws Exception {
        
        when(jwtService.validateJwtToken("mockRefreshToken")).thenReturn(true);
        when(jwtService.getEmailFromToken("mockRefreshToken")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));
        when(jwtService.refreshBaseToken("test@example.com", "mockRefreshToken")).thenReturn(jwtAuthenticationDto);

        
        JwtAuthenticationDto result = userService.refreshToken(refreshTokenDto);

        
        assertNotNull(result);
        assertEquals("mockAccessToken", result.getToken());
        assertEquals("mockRefreshToken", result.getRefreshToken());
        verify(jwtService, times(1)).validateJwtToken("mockRefreshToken");
        verify(jwtService, times(1)).getEmailFromToken("mockRefreshToken");
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(jwtService, times(1)).refreshBaseToken("test@example.com", "mockRefreshToken");
    }

    @Test
    void testRefreshToken_InvalidToken_ThrowsException() {
        
        when(jwtService.validateJwtToken("mockRefreshToken")).thenReturn(false);

        
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> userService.refreshToken(refreshTokenDto));
        assertEquals("Недействительный refresh token", exception.getMessage());
        verify(jwtService, times(1)).validateJwtToken("mockRefreshToken");
        verify(jwtService, never()).getEmailFromToken(any());
        verify(userRepository, never()).findByEmail(any());
        verify(jwtService, never()).refreshBaseToken(any(), any());
    }

    @Test
    void testRegisterUser_Successful() {
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userMapper.toEntity(userRegisterDTO, passwordEncoder)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toUserDTO(userEntity)).thenReturn(userDTO);

        
        UserDTO result = userService.registerUser(userRegisterDTO);

        
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("John Doe", result.getFullName());
        assertEquals(RoleUsers.ROLE_USER, result.getRole());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userMapper, times(1)).toEntity(userRegisterDTO, passwordEncoder);
        verify(userRepository, times(1)).save(userEntity);
        verify(userMapper, times(1)).toUserDTO(userEntity);
    }

    @Test
    void testRegisterUser_DuplicateEmail_ThrowsException() {
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));

        
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> userService.registerUser(userRegisterDTO));
        assertEquals("Почта уже существует", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userMapper, never()).toEntity(any(), any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toUserDTO(any());
    }

    @Test
    void testGetUser_Successful_AsAdmin() {
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(authentication.getName()).thenReturn("admin@example.com");
        when(userMapper.toUserDTO(userEntity)).thenReturn(userDTO);
        try (MockedStatic<AuthUtils> mocked = mockStatic(AuthUtils.class)) {
            mocked.when(() -> AuthUtils.isAdmin(authentication)).thenReturn(true);

            
            UserDTO result = userService.getUser(1L);

            
            assertNotNull(result);
            assertEquals("test@example.com", result.getEmail());
            assertEquals("John Doe", result.getFullName());
            assertEquals(RoleUsers.ROLE_USER, result.getRole());
            verify(userRepository, times(1)).findById(1L);
            verify(userMapper, times(1)).toUserDTO(userEntity);
        }
    }

    @Test
    void testGetUser_Successful_AsSameUser() {
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(authentication.getName()).thenReturn("test@example.com");
        when(userMapper.toUserDTO(userEntity)).thenReturn(userDTO);
        try (MockedStatic<AuthUtils> mocked = mockStatic(AuthUtils.class)) {
            mocked.when(() -> AuthUtils.isAdmin(authentication)).thenReturn(false);

            
            UserDTO result = userService.getUser(1L);

            
            assertNotNull(result);
            assertEquals("test@example.com", result.getEmail());
            assertEquals("John Doe", result.getFullName());
            assertEquals(RoleUsers.ROLE_USER, result.getRole());
            verify(userRepository, times(1)).findById(1L);
            verify(userMapper, times(1)).toUserDTO(userEntity);
        }
    }

    @Test
    void testGetUser_UserNotFound_ThrowsException() {
        
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getUser(1L));
        assertEquals("Пользователь не найден", exception.getMessage());
        assertEquals(404, exception.getStatus());
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, never()).toUserDTO(any());
    }

    @Test
    void testGetUser_AccessDenied_ThrowsException() {
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(authentication.getName()).thenReturn("other@example.com");
        try (MockedStatic<AuthUtils> mocked = mockStatic(AuthUtils.class)) {
            mocked.when(() -> AuthUtils.isAdmin(authentication)).thenReturn(false);

            
            AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> userService.getUser(1L));
            assertEquals("Доступ запрещен", exception.getMessage());
            verify(userRepository, times(1)).findById(1L);
            verify(userMapper, never()).toUserDTO(any());
        }
    }

    @Test
    void testGetCurrentUser_Successful() {
        
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));
        when(userMapper.toUserDTO(userEntity)).thenReturn(userDTO);

        
        UserDTO result = userService.getCurrentUser();

        
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("John Doe", result.getFullName());
        assertEquals(RoleUsers.ROLE_USER, result.getRole());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userMapper, times(1)).toUserDTO(userEntity);
    }

    @Test
    void testGetCurrentUser_UserNotFound_ThrowsException() {
        
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getCurrentUser());
        assertEquals("Пользователь не найден", exception.getMessage());
        assertEquals(404, exception.getStatus());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userMapper, never()).toUserDTO(any());
    }

    @Test
    void testGetUserCards_Successful_AsAdmin() {
        
        userEntity.setCardEntities(List.of(cardEntity));
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(authentication.getName()).thenReturn("admin@example.com");
        when(cardMapper.toCardDTO(cardEntity)).thenReturn(cardDTO);
        try (MockedStatic<AuthUtils> mocked = mockStatic(AuthUtils.class)) {
            mocked.when(() -> AuthUtils.isAdmin(authentication)).thenReturn(true);

            
            List<CardDTO> result = userService.getUserCards(1L);

            
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(cardDTO, result.get(0));
            assertEquals("**** **** **** 3456", result.get(0).getMaskedNumber());
            assertEquals(CardStatus.ACTIVE, result.get(0).getStatus());
            verify(userRepository, times(1)).findById(1L);
            verify(cardMapper, times(1)).toCardDTO(cardEntity);
        }
    }

    @Test
    void testGetUserCards_Successful_AsSameUser() {
        
        userEntity.setCardEntities(List.of(cardEntity));
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(authentication.getName()).thenReturn("test@example.com");
        when(cardMapper.toCardDTO(cardEntity)).thenReturn(cardDTO);
        try (MockedStatic<AuthUtils> mocked = mockStatic(AuthUtils.class)) {
            mocked.when(() -> AuthUtils.isAdmin(authentication)).thenReturn(false);

            
            List<CardDTO> result = userService.getUserCards(1L);

            
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(cardDTO, result.get(0));
            assertEquals("**** **** **** 3456", result.get(0).getMaskedNumber());
            assertEquals(CardStatus.ACTIVE, result.get(0).getStatus());
            verify(userRepository, times(1)).findById(1L);
            verify(cardMapper, times(1)).toCardDTO(cardEntity);
        }
    }

    @Test
    void testGetUserCards_UserNotFound_ThrowsException() {
        
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getUserCards(1L));
        assertEquals("Пользователь не найден", exception.getMessage());
        assertEquals(404, exception.getStatus());
        verify(userRepository, times(1)).findById(1L);
        verify(cardMapper, never()).toCardDTO(any());
    }

    @Test
    void testGetUserCards_AccessDenied_ThrowsException() {
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(authentication.getName()).thenReturn("other@example.com");
        try (MockedStatic<AuthUtils> mocked = mockStatic(AuthUtils.class)) {
            mocked.when(() -> AuthUtils.isAdmin(authentication)).thenReturn(false);

            
            AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> userService.getUserCards(1L));
            assertEquals("Доступ запрещен", exception.getMessage());
            verify(userRepository, times(1)).findById(1L);
            verify(cardMapper, never()).toCardDTO(any());
        }
    }

    @Test
    void testGetAllUsers_Successful() {
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> userPage = new PageImpl<>(List.of(userEntity));
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toUserDTO(userEntity)).thenReturn(userDTO);

        
        Page<UserDTO> result = userService.getAllUsers(pageable);

        
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(userDTO, result.getContent().get(0));
        verify(userRepository, times(1)).findAll(pageable);
        verify(userMapper, times(1)).toUserDTO(userEntity);
    }


    @Test
    void testRefreshToken_UserNotFound_ThrowsException() throws Exception {
        
        when(jwtService.validateJwtToken("mockRefreshToken")).thenReturn(true);
        when(jwtService.getEmailFromToken("mockRefreshToken")).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.refreshToken(refreshTokenDto));
        assertEquals("Пользователь с email nonexistent@example.com не найден", exception.getMessage());
        assertEquals(404, exception.getStatus());
        verify(jwtService, times(1)).validateJwtToken("mockRefreshToken");
        verify(jwtService, times(1)).getEmailFromToken("mockRefreshToken");
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        verify(jwtService, never()).refreshBaseToken(any(), any());
    }

    @Test
    void testGetAllUsers_EmptyResult() {
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> emptyPage = new PageImpl<>(Collections.emptyList());
        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        
        Page<UserDTO> result = userService.getAllUsers(pageable);


        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(userRepository, times(1)).findAll(pageable);
        verify(userMapper, never()).toUserDTO(any());
    }

    @Test
    void testGetUserCards_EmptyCardList() {
        
        userEntity.setCardEntities(Collections.emptyList());
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(authentication.getName()).thenReturn("test@example.com");
        try (MockedStatic<AuthUtils> mocked = mockStatic(AuthUtils.class)) {
            mocked.when(() -> AuthUtils.isAdmin(authentication)).thenReturn(false);

            
            List<CardDTO> result = userService.getUserCards(1L);

            
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(userRepository, times(1)).findById(1L);
            verify(cardMapper, never()).toCardDTO(any());
        }
    }

    @Test
    void testGetUserCards_MultipleCards() {
        
        CardEntity cardEntity2 = new CardEntity();
        cardEntity2.setId(2L);
        cardEntity2.setNumber("9876543210987654");
        cardEntity2.setStatus(CardStatus.BLOCK);

        CardDTO cardDTO2 = new CardDTO();
        cardDTO2.setMaskedNumber("**** **** **** 7654");
        cardDTO2.setStatus(CardStatus.BLOCK);

        userEntity.setCardEntities(List.of(cardEntity, cardEntity2));
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(authentication.getName()).thenReturn("test@example.com");
        when(cardMapper.toCardDTO(cardEntity)).thenReturn(cardDTO);
        when(cardMapper.toCardDTO(cardEntity2)).thenReturn(cardDTO2);

        try (MockedStatic<AuthUtils> mocked = mockStatic(AuthUtils.class)) {
            mocked.when(() -> AuthUtils.isAdmin(authentication)).thenReturn(false);

            
            List<CardDTO> result = userService.getUserCards(1L);

            
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("**** **** **** 3456", result.get(0).getMaskedNumber());
            assertEquals(CardStatus.ACTIVE, result.get(0).getStatus());
            assertEquals("**** **** **** 7654", result.get(1).getMaskedNumber());
            assertEquals(CardStatus.BLOCK, result.get(1).getStatus());
            verify(userRepository, times(1)).findById(1L);
            verify(cardMapper, times(1)).toCardDTO(cardEntity);
            verify(cardMapper, times(1)).toCardDTO(cardEntity2);
        }
    }
    
}