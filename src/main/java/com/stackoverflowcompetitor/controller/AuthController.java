package com.stackoverflowcompetitor.controller;

import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.service.UserService;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Registers a new user.
     *
     * @param user (the user to be registered)
     * @return (ResponseEntity with the registration status)
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error while registering user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to register user: " + e.getMessage());
        }
    }

    /**
     * Logs in a user.
     *
     * @param username (the username)
     * @param password (the password)
     * @param request  (the HttpServletRequest)
     * @return (ResponseEntity with the login status)
     */
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        try {
            String message = userService.loginUser(username, password, request);
            return ResponseEntity.ok(message);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to login user: " + e.getMessage());
        }
    }

    /**
     * Logs out a user.
     *
     * @param request  (the HttpServletRequest)
     * @param response (the HttpServletResponse)
     * @return (ResponseEntity with the logout status)
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            userService.logoutUser(request, response);
            return ResponseEntity.ok("User logged out successfully");
        } catch (Exception e) {
            log.error("Error while logging out user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to logout user: " + e.getMessage());
        }
    }

    // Test methods for development purpose
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("Test endpoint hit");
        return ResponseEntity.status(HttpStatus.OK).body("Test endpoint hit");
    }
}