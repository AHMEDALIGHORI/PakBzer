package com.pakbzer.security;

import com.pakbzer.config.DebugLog;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(0)
public class GoogleOAuthDebugFilter extends OncePerRequestFilter {

    private final String redirectUri;

    public GoogleOAuthDebugFilter(@Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.redirectUri = baseUrl + "/login/oauth2/code/google";
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri != null && uri.startsWith("/oauth2/authorization/")) {
            // #region agent log
            DebugLog.write("H-B", "GoogleOAuthDebugFilter:doFilterInternal",
                    "OAuth authorization redirect initiated",
                    "{\"requestUri\":\"" + uri
                            + "\",\"redirectUri\":\"" + redirectUri
                            + "\",\"clientIdSuffix\":\"...dh47g\"}");
            // #endregion
        }
        filterChain.doFilter(request, response);
    }
}
