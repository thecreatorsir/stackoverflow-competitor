package com.stackoverflowcompetitor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    @Mock
    private UserService userService;

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
    void testRegisterUser_ValidationFailure() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");

        doThrow(new ValidationException("Validation error")).when(userService).registerUser(any(User.class));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Validation error"));

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
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to register user: Registration failed"));

        verify(userService, times(1)).registerUser(any(User.class));
    }


    @Test
    void testLoginUser_Success() throws Exception {
        String username = "testuser";
        String password = "testpassword";

        when(userService.loginUser(eq(username), eq(password), any(HttpServletRequest.class)))
                .thenReturn("User logged in successfully");

        mockMvc.perform(post("/auth/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(content().string("User logged in successfully"));

        verify(userService, times(1)).loginUser(eq(username), eq(password), any(HttpServletRequest.class));
    }



    @Test
    void testLoginUser_BadCredentials() throws Exception {
        String username = "testuser";
        String password = "invalidpassword";
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(userService.loginUser(eq(username), eq(password), any(HttpServletRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));

        verify(userService, times(1)).loginUser(eq(username), eq(password), any(HttpServletRequest.class));
    }


    @Test
    void testLoginUser_Failure() throws Exception {
        String username = "testuser";
        String password = "testpassword";
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(userService.loginUser(eq(username), eq(password), any(HttpServletRequest.class)))
                .thenThrow(new RuntimeException("Login failed"));

        mockMvc.perform(post("/auth/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to login user: Login failed"));

        verify(userService, times(1)).loginUser(eq(username), eq(password), any(HttpServletRequest.class));
    }


    @Test
    void testLogoutUser_Success() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        MockHttpServletResponse response = new MockHttpServletResponse();

        doNothing().when(userService).logoutUser(request, response);

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("User logged out successfully"));

        verify(userService, times(1)).logoutUser(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    void testLogoutUser_Failure() throws Exception {
        doThrow(new RuntimeException("Logout failed")).when(userService).logoutUser(any(HttpServletRequest.class), any(HttpServletResponse.class));

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to logout user: Logout failed"));

        verify(userService, times(1)).logoutUser(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

}
