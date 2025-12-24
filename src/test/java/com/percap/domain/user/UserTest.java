package com.percap.domain.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user-id-123");
        user.setLogin("testuser");
        user.setPassword("encoded-password");
        user.setRole(UserRole.USER);
    }

    @Test
    void testGetAuthorities_WithRole() {
        // Act
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Assert
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testGetAuthorities_WithAdminRole() {
        // Arrange
        user.setRole(UserRole.ADMIN);

        // Act
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Assert
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testGetAuthorities_WithoutRole() {
        // Arrange
        user.setRole(null);

        // Act
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Assert
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    void testGetUsername() {
        // Act
        String username = user.getUsername();

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    void testIsAccountNonExpired() {
        // Act & Assert
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        // Act & Assert
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        // Act & Assert
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        // Act & Assert
        assertTrue(user.isEnabled());
    }

    @Test
    void testUserDetailsImplementation() {
        // Assert
        assertTrue(user instanceof UserDetails);
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        User newUser = new User();
        String id = "new-id";
        String login = "newlogin";
        String password = "newpassword";
        UserRole role = UserRole.ADMIN;

        // Act
        newUser.setId(id);
        newUser.setLogin(login);
        newUser.setPassword(password);
        newUser.setRole(role);

        // Assert
        assertEquals(id, newUser.getId());
        assertEquals(login, newUser.getLogin());
        assertEquals(password, newUser.getPassword());
        assertEquals(role, newUser.getRole());
    }
}

