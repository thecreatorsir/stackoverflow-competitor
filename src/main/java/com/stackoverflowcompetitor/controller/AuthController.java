package com.stackoverflowcompetitor.controller;

import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Registers a new user.
     *
     * @param user (the user to be registered)
     * @return (ResponseEntity with the registration status)
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            log.info("Registering user: {}", user.getUsername());
            userService.registerUser(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
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
            log.info("Attempting login for user: {}", username);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            log.info("User logged in successfully: {}", username);
            return ResponseEntity.ok("User logged in successfully");
        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for user: {}", username);
            return ResponseEntity.status(401).body("Invalid username or password");
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(500).body("Login failed: " + e.getMessage());
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
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                log.info("Logging out user: {}", auth.getName());
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            return ResponseEntity.ok("User logged out successfully");
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.status(500).body("Logout failed: " + e.getMessage());
        }
    }

    // Test methods for development purpose
    @GetMapping("/test")
    public int test() {
        log.info("Test endpoint hit");
        return 1/0;
    }
}