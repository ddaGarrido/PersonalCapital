package com.percap.repositories;

import com.percap.domain.user.User;
import com.percap.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setLogin("testuser");
        testUser.setPassword("encoded-password");
        testUser.setRole(UserRole.USER);
    }

    @Test
    void testFindByLogin_UserExists() {
        // Arrange
        entityManager.persistAndFlush(testUser);

        // Act
        Optional<User> found = userRepository.findByLogin("testuser");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getLogin());
        assertEquals(UserRole.USER, found.get().getRole());
    }

    @Test
    void testFindByLogin_UserDoesNotExist() {
        // Act
        Optional<User> found = userRepository.findByLogin("nonexistent");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void testSave_NewUser() {
        // Act
        User saved = userRepository.save(testUser);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("testuser", saved.getLogin());
        assertEquals(UserRole.USER, saved.getRole());
    }

    @Test
    void testSave_UserWithAdminRole() {
        // Arrange
        testUser.setRole(UserRole.ADMIN);

        // Act
        User saved = userRepository.save(testUser);

        // Assert
        assertNotNull(saved.getId());
        assertEquals(UserRole.ADMIN, saved.getRole());
    }

    @Test
    void testSave_UserWithDefaultRole() {
        // Arrange
        testUser.setRole(null);

        // Act
        User saved = userRepository.save(testUser);

        // Assert
        assertNotNull(saved.getId());
        assertNull(saved.getRole());
    }

    @Test
    void testFindByLogin_CaseSensitive() {
        // Arrange
        entityManager.persistAndFlush(testUser);

        // Act
        Optional<User> found = userRepository.findByLogin("TESTUSER");

        // Assert
        // Should be case-sensitive, so should not find the user
        assertFalse(found.isPresent());
    }

    @Test
    void testSave_MultipleUsers() {
        // Arrange
        User user1 = new User();
        user1.setLogin("user1");
        user1.setPassword("password1");
        user1.setRole(UserRole.USER);

        User user2 = new User();
        user2.setLogin("user2");
        user2.setPassword("password2");
        user2.setRole(UserRole.ADMIN);

        // Act
        User saved1 = userRepository.save(user1);
        User saved2 = userRepository.save(user2);

        // Assert
        assertNotNull(saved1.getId());
        assertNotNull(saved2.getId());
        assertNotEquals(saved1.getId(), saved2.getId());

        Optional<User> found1 = userRepository.findByLogin("user1");
        Optional<User> found2 = userRepository.findByLogin("user2");

        assertTrue(found1.isPresent());
        assertTrue(found2.isPresent());
        assertEquals(UserRole.USER, found1.get().getRole());
        assertEquals(UserRole.ADMIN, found2.get().getRole());
    }
}

