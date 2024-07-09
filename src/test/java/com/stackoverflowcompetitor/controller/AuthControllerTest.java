package com.stackoverflowcompetitor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        verify(userService, times(1)).registerUser(any(User.class));
    }

    @Test
    void testRegisterUser_Failure() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");

        doThrow(new RuntimeException("Registration failed")).when(userService).registerUser(any(User.class));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Registration failed: Registration failed"));

        verify(userService, times(1)).registerUser(any(User.class));
    }

    @Test
    void testLoginUser_Success() throws Exception {
        String username = "testuser";
        String password = "testpassword";

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(post("/auth/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(content().string("User logged in successfully"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLoginUser_BadCredentials() throws Exception {
        String username = "testuser";
        String password = "invalidpassword";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLoginUser_Failure() throws Exception {
        String username = "testuser";
        String password = "testpassword";

        doThrow(new RuntimeException("Login failed")).when(authenticationManager).authenticate(any());

        mockMvc.perform(post("/auth/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Login failed: Login failed"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLogoutUser_Success() throws Exception {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MockHttpServletResponse response = new MockHttpServletResponse();

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("User logged out successfully"));

        verify(authentication, times(1)).getName();
    }

    @Test
    void testLogoutUser_Failure() throws Exception {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MockHttpServletResponse response = new MockHttpServletResponse();

        doThrow(new RuntimeException("Logout failed")).when(authentication).getName();

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Logout failed: Logout failed"));

        verify(authentication, times(1)).getName();
    }
}
