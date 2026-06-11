package com.pakbzer.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    private final CustomOidcUserService oidcUserService;

    public SecurityConfig(CustomOidcUserService oidcUserService) {
        this.oidcUserService = oidcUserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/home",
                                "/category/**", "/product/**", "/search",
                                "/cart/**",
                                "/register", "/login",
                                "/css/**", "/js/**", "/images/**", "/webjars/**",
                                "/h2-console/**")
                        .permitAll()
                        .requestMatchers("/checkout/**", "/review/**", "/account/**")
                        .authenticated()
                        .anyRequest().permitAll())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService)))
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/?loggedout")
                        .permitAll());

        // H2 console runs inside a frame and uses its own (non-Spring) forms.
        http.csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")));
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
