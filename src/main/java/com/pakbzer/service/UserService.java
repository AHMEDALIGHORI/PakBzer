package com.pakbzer.service;

import com.pakbzer.model.Role;
import com.pakbzer.model.User;
import com.pakbzer.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new local (email + password) account.
     *
     * @throws IllegalArgumentException if the email is already taken.
     */
    public User register(String fullName, String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        User user = new User(email, fullName, passwordEncoder.encode(rawPassword), Role.USER, "LOCAL");
        return userRepository.save(user);
    }

    /**
     * Returns the existing Google user or provisions one on first OAuth login.
     */
    public User findOrCreateGoogleUser(String email, String fullName) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User(email, fullName, null, Role.USER, "GOOGLE");
            return userRepository.save(user);
        });
    }
}
