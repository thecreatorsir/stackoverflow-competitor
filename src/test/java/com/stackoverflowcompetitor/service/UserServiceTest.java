package com.stackoverflowcompetitor.service;

import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.repository.UserRepository;
import com.stackoverflowcompetitor.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
class UserServiceTest {

    @Mock
    private UserRepository userRepository;


    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.registerUser(user);

        assertEquals("encodedPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
        verify(passwordEncoder, times(1)).encode("password");
    }

    @Test
    void testRegisterUser_UsernameTooShort() {
        User user = new User();
        user.setUsername("usr");
        user.setPassword("password");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.registerUser(user);
        });

        assertEquals("Username length must be between " + Constants.MIN_USERNAME_LENGTH + " and " + Constants.MAX_USERNAME_LENGTH + " characters", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testRegisterUser_PasswordTooShort() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("pwd");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.registerUser(user);
        });

        assertEquals("Password length must be between " + Constants.MIN_PASSWORD_LENGTH + " and " + Constants.MAX_PASSWORD_LENGTH + " characters", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testRegisterUser_Failure() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(user);
        });

        assertEquals("Database error", exception.getMessage());
        verify(userRepository, times(1)).save(user);
        verify(passwordEncoder, times(1)).encode("password");
    }

    @Test
    void testFindByUsername_Success() {
        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User foundUser = userService.findByUsername("testuser");

        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testFindByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        User foundUser = userService.findByUsername("nonexistent");

        assertNull(foundUser);
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testLoginUser_Success() {
        String username = "testuser";
        String password = "password";
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getSession(true)).thenReturn(session);
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        String result = userService.loginUser(username, password, request);

        assertEquals("User logged in successfully", result);
        verify(request, times(1)).getSession(true);
        verify(session, times(1)).setAttribute(eq("SPRING_SECURITY_CONTEXT"), any());
    }

    @Test
    void testLoginUser_BadCredentials() {
        String username = "testuser";
        String password = "password";
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            userService.loginUser(username, password, request);
        });

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testLoginUser_Failure() {
        String username = "testuser";
        String password = "password";
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Login failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.loginUser(username, password, request);
        });

        assertEquals("Login failed: Login failed", exception.getMessage());
    }

    @Test
    void testLogoutUser_Success() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        userService.logoutUser(request, response);

        verify(authentication, times(1)).getName();
    }
}

