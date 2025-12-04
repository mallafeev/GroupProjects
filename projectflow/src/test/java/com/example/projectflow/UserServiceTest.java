package com.example.projectflow;

import com.example.projectflow.model.User;
import com.example.projectflow.repository.UserRepository;
import com.example.projectflow.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = UserService.class)
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testRegister_Success() {
        // Given
        String login = "test@example.com";
        String password = "123456";

        User newUser = new User();
        newUser.setLogin(login);
        newUser.setPasswordHash(password);

        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When
        User result = userService.register(login, password);

        // Then
        assertNotNull(result);
        assertEquals(login, result.getLogin());
        assertEquals(password, result.getPasswordHash());

        verify(userRepository).findByLogin(login);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_UserAlreadyExists() {
        // Given
        String login = "test@example.com";
        String password = "123456";

        User existingUser = new User();
        existingUser.setLogin(login);

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(existingUser));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.register(login, password);
        });

        verify(userRepository).findByLogin(login);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testAuthenticate_Success() {
        // Given
        String login = "test@example.com";
        String password = "123456";

        User user = new User();
        user.setLogin(login);
        user.setPasswordHash(password);

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.authenticate(login, password);

        // Then
        assertTrue(result.isPresent());
        assertEquals(login, result.get().getLogin());

        verify(userRepository).findByLogin(login);
    }

    @Test
    void testAuthenticate_WrongPassword() {
        // Given
        String login = "test@example.com";
        String correctPassword = "123456";
        String wrongPassword = "wrongpass";

        User user = new User();
        user.setLogin(login);
        user.setPasswordHash(correctPassword);

        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.authenticate(login, wrongPassword);

        // Then
        assertTrue(result.isEmpty());

        verify(userRepository).findByLogin(login);
    }

    @Test
    void testAuthenticate_UserNotFound() {
        // Given
        String login = "notfound@example.com";
        String password = "123456";

        when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.authenticate(login, password);

        // Then
        assertTrue(result.isEmpty());

        verify(userRepository).findByLogin(login);
    }

    @Test
    void testGetAllUsers() {
        // Given
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertEquals(2, result.size());
        assertEquals(users, result);

        verify(userRepository).findAll();
    }
}