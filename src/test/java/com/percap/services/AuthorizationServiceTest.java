package com.percap.services;

import com.percap.domain.user.User;
import com.percap.domain.user.UserRole;
import com.percap.dtos.auth.LoginResponseDTO;
import com.percap.dtos.auth.RegisterDTO;
import com.percap.infra.security.TokenService;
import com.percap.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthorizationService authorizationService;

    private User testUser;
    private String encodedPassword;

    @BeforeEach
    void setUp() {
        encodedPassword = "$2a$10$encodedPasswordHash";
        testUser = new User();
        testUser.setId("user-id-123");
        testUser.setLogin("testuser");
        testUser.setPassword(encodedPassword);
        testUser.setRole(UserRole.USER);
    }

    @Test
    void testAuthenticate_Success() {
        // Arrange
        String login = "testuser";
        String password = "password123";
        String token = "generated-token";

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(tokenService.generateToken(testUser)).thenReturn(token);

        // Act
        LoginResponseDTO result = authorizationService.authenticate(login, password);

        // Assert
        assertNotNull(result);
        assertEquals(token, result.getToken());
        assertEquals(login, result.getLogin());
        assertEquals(UserRole.USER, result.getRole());
        verify(userRepository).findByLogin(login);
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(tokenService).generateToken(testUser);
    }

    @Test
    void testAuthenticate_UserNotFound() {
        // Arrange
        String login = "nonexistent";
        String password = "password123";

        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            authorizationService.authenticate(login, password);
        });

        verify(userRepository).findByLogin(login);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(tokenService, never()).generateToken(any(User.class));
    }

    @Test
    void testAuthenticate_InvalidPassword() {
        // Arrange
        String login = "testuser";
        String password = "wrongpassword";

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            authorizationService.authenticate(login, password);
        });

        verify(userRepository).findByLogin(login);
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(tokenService, never()).generateToken(any(User.class));
    }

    @Test
    void testRegister_Success() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO("newuser", "password123", UserRole.USER);
        User savedUser = new User();
        savedUser.setId("new-user-id");
        savedUser.setLogin("newuser");
        savedUser.setPassword(encodedPassword);
        savedUser.setRole(UserRole.USER);
        String token = "generated-token";

        when(userRepository.findByLogin("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenService.generateToken(savedUser)).thenReturn(token);

        // Act
        LoginResponseDTO result = authorizationService.register(registerDTO);

        // Assert
        assertNotNull(result);
        assertEquals(token, result.getToken());
        assertEquals("newuser", result.getLogin());
        assertEquals(UserRole.USER, result.getRole());
        verify(userRepository).findByLogin("newuser");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(tokenService).generateToken(savedUser);
    }

    @Test
    void testRegister_UserAlreadyExists() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO("existinguser", "password123", UserRole.USER);

        when(userRepository.findByLogin("existinguser")).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authorizationService.register(registerDTO);
        });

        verify(userRepository).findByLogin("existinguser");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(tokenService, never()).generateToken(any(User.class));
    }

    @Test
    void testRegister_DefaultRole() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO("newuser", "password123", null);
        User savedUser = new User();
        savedUser.setId("new-user-id");
        savedUser.setLogin("newuser");
        savedUser.setPassword(encodedPassword);
        savedUser.setRole(UserRole.USER);
        String token = "generated-token";

        when(userRepository.findByLogin("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenService.generateToken(savedUser)).thenReturn(token);

        // Act
        LoginResponseDTO result = authorizationService.register(registerDTO);

        // Assert
        assertNotNull(result);
        assertEquals(UserRole.USER, result.getRole());
        verify(userRepository).save(argThat(user -> user.getRole() == UserRole.USER));
    }

    @Test
    void testRegister_WithAdminRole() {
        // Arrange
        RegisterDTO registerDTO = new RegisterDTO("adminuser", "password123", UserRole.ADMIN);
        User savedUser = new User();
        savedUser.setId("admin-user-id");
        savedUser.setLogin("adminuser");
        savedUser.setPassword(encodedPassword);
        savedUser.setRole(UserRole.ADMIN);
        String token = "generated-token";

        when(userRepository.findByLogin("adminuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenService.generateToken(savedUser)).thenReturn(token);

        // Act
        LoginResponseDTO result = authorizationService.register(registerDTO);

        // Assert
        assertNotNull(result);
        assertEquals(UserRole.ADMIN, result.getRole());
        verify(userRepository).save(argThat(user -> user.getRole() == UserRole.ADMIN));
    }
}

