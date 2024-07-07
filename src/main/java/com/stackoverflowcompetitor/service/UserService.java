package com.stackoverflowcompetitor.service;

import com.stackoverflowcompetitor.model.User;
import com.stackoverflowcompetitor.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public void registerUser(User user) {
        log.info("In a registerUser method");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        log.info("In a findByUsername method");
        return userRepository.findByUsername(username).orElse(null);
    }
}