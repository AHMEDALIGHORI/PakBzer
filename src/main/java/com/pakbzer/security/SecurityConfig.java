package com.pakbzer.security;

import com.pakbzer.config.DebugLog;
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
                                "/h2-console/**",
                                "/api/webhooks/**")
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
                        .failureUrl("/login?oauth_error")
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService))
                        .failureHandler((request, response, exception) -> {
                            String msg = exception.getMessage() == null ? "unknown" : exception.getMessage();
                            if (msg.length() > 200) {
                                msg = msg.substring(0, 200);
                            }
                            // #region agent log
                            DebugLog.write("H-B", "SecurityConfig:oauthFailureHandler",
                                    "OAuth2 login failure handler",
                                    "{\"requestUri\":\"" + request.getRequestURI()
                                            + "\",\"errorMessage\":\"" + msg.replace("\"", "'").replace("\n", " ") + "\"}");
                            // #endregion
                            response.sendRedirect("/login?oauth_error");
                        }))
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/?loggedout")
                        .permitAll());

        // H2 console runs inside a frame and uses its own (non-Spring) forms.
        http.csrf(csrf -> csrf.ignoringRequestMatchers(
                new AntPathRequestMatcher("/h2-console/**"),
                new AntPathRequestMatcher("/api/webhooks/**")));
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
