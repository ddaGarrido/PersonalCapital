package com.percap.infra.security;

import com.percap.domain.user.User;
import com.percap.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private User testUser;
    private static final String TEST_SECRET = "test-secret-key-for-jwt-token-generation-minimum-256-bits-required-for-security";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "secret", TEST_SECRET);
        
        testUser = new User();
        testUser.setId("user-id-123");
        testUser.setLogin("testuser");
        testUser.setRole(UserRole.USER);
    }

    @Test
    void testGenerateToken_Success() {
        // Act
        String token = tokenService.generateToken(testUser);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void testGenerateToken_DifferentUsers_DifferentTokens() {
        // Arrange
        User user1 = new User();
        user1.setLogin("user1");
        user1.setRole(UserRole.USER);

        User user2 = new User();
        user2.setLogin("user2");
        user2.setRole(UserRole.USER);

        // Act
        String token1 = tokenService.generateToken(user1);
        String token2 = tokenService.generateToken(user2);

        // Assert
        assertNotEquals(token1, token2);
    }

    @Test
    void testValidateToken_ValidToken() {
        // Arrange
        String token = tokenService.generateToken(testUser);

        // Act
        String login = tokenService.validateToken(token);

        // Assert
        assertNotNull(login);
        assertEquals("testuser", login);
    }

    @Test
    void testValidateToken_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        String login = tokenService.validateToken(invalidToken);

        // Assert
        assertNull(login);
    }

    @Test
    void testValidateToken_EmptyToken() {
        // Act
        String login = tokenService.validateToken("");

        // Assert
        assertNull(login);
    }

    @Test
    void testValidateToken_NullToken() {
        // Act
        String login = tokenService.validateToken(null);

        // Assert
        assertNull(login);
    }

    @Test
    void testValidateToken_TokenWithDifferentSecret() {
        // Arrange
        String token = tokenService.generateToken(testUser);
        
        // Change the secret
        ReflectionTestUtils.setField(tokenService, "secret", "different-secret-key-for-jwt-token-generation-minimum-256-bits");

        // Act
        String login = tokenService.validateToken(token);

        // Assert
        assertNull(login);
    }

    @Test
    void testGenerateAndValidateToken_RoundTrip() {
        // Arrange
        User user = new User();
        user.setLogin("roundtripuser");
        user.setRole(UserRole.ADMIN);

        // Act
        String token = tokenService.generateToken(user);
        String validatedLogin = tokenService.validateToken(token);

        // Assert
        assertNotNull(token);
        assertEquals("roundtripuser", validatedLogin);
    }
}

