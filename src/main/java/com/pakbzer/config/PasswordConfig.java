package com.pakbzer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Dedicated config for the password encoder. Kept separate from SecurityConfig
 * so that beans needing a PasswordEncoder (e.g. UserService) do not create a
 * dependency cycle with SecurityConfig (which itself depends on services).
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
