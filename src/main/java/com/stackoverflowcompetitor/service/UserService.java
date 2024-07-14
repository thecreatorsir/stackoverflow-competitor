package com.stackoverflowcompetitor.service;

import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.repository.UserRepository;
import com.stackoverflowcompetitor.util.Constants;
import com.stackoverflowcompetitor.util.ValidationUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public void registerUser(User user) {
        if(ValidationUtil.validateLength(user.getUsername(), Constants.MIN_USERNAME_LENGTH, Constants.MAX_USERNAME_LENGTH)){
            log.error("Invalid username length");
            throw new ValidationException("Username length must be between " + Constants.MIN_USERNAME_LENGTH + " and " + Constants.MAX_USERNAME_LENGTH + " characters");
        }

        if(ValidationUtil.validateLength(user.getPassword(), Constants.MIN_PASSWORD_LENGTH, Constants.MAX_PASSWORD_LENGTH)){
            log.error("Invalid password length");
            throw new ValidationException("Password length must be between " + Constants.MIN_PASSWORD_LENGTH + " and " + Constants.MAX_PASSWORD_LENGTH + " characters");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());
    }

    public String loginUser(String username, String password, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            log.info("User logged in successfully: {}", username);
            return "User logged in successfully";
        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for user: {}", username);
            throw new BadCredentialsException("Invalid username or password");
        } catch (Exception e) {
            log.error("Login failed for user: {}", username, e);
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }


    public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            log.info("Logging out user: {}", auth.getName());
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    }

    public User findByUsername(String username) {
        log.info("Finding user by username: {}", username);
        return userRepository.findByUsername(username).orElse(null);
    }
}