package com.pakbzer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component
public class GoogleOAuthDebugListener implements ApplicationRunner {

    private final StripeProperties stripeProperties;

    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret:}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri:}")
    private String redirectUri;

    public GoogleOAuthDebugListener(StripeProperties stripeProperties) {
        this.stripeProperties = stripeProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean idPresent = clientId != null && !clientId.isBlank();
        boolean secretPresent = clientSecret != null && !clientSecret.isBlank();
        String idPrefix = idPresent && clientId.length() > 12
                ? clientId.substring(0, 12) + "..."
                : (idPresent ? "short" : "empty");
        // #region agent log
        DebugLog.write("H-A", "GoogleOAuthDebugListener:startup",
                "OAuth env vars at startup",
                "{\"clientIdPresent\":" + idPresent
                        + ",\"clientSecretPresent\":" + secretPresent
                        + ",\"clientIdPrefix\":\"" + idPrefix + "\""
                        + ",\"redirectUri\":\"" + (redirectUri == null ? "" : redirectUri.replace("\"", "'")) + "\"}");
        // #endregion
        // #region agent log
        DebugLog.write("H-E", "GoogleOAuthDebugListener:startup",
                "Google OAuth client registration viability",
                "{\"registrationViable\":" + (idPresent && secretPresent) + "}");
        // #endregion
        // #region agent log
        DebugLog.write("STRIPE", "GoogleOAuthDebugListener:startup",
                "Stripe env vars at startup",
                "{\"stripeConfigured\":" + stripeProperties.isConfigured() + "}");
        // #endregion
    }

    @EventListener
    public void onAuthSuccess(AuthenticationSuccessEvent event) {
        var auth = event.getAuthentication();
        boolean isOAuth = auth instanceof OAuth2AuthenticationToken;
        String provider = isOAuth ? ((OAuth2AuthenticationToken) auth).getAuthorizedClientRegistrationId() : "non-oauth";
        String principalName = auth.getName();
        boolean emailVerified = false;
        if (auth.getPrincipal() instanceof OidcUser oidcUser) {
            emailVerified = Boolean.TRUE.equals(oidcUser.getEmailVerified());
        }
        // #region agent log
        DebugLog.write("H-D", "GoogleOAuthDebugListener:onAuthSuccess",
                "Authentication success event",
                "{\"isOAuth\":" + isOAuth
                        + ",\"provider\":\"" + provider + "\""
                        + ",\"principalName\":\"" + (principalName == null ? "" : principalName.replace("\"", "'")) + "\""
                        + ",\"emailVerified\":" + emailVerified
                        + ",\"authenticated\":" + auth.isAuthenticated() + "}");
        // #endregion
    }

    @EventListener
    public void onAuthFailure(AbstractAuthenticationFailureEvent event) {
        String type = event.getClass().getSimpleName();
        String message = event.getException() != null ? event.getException().getMessage() : "unknown";
        if (message != null && message.length() > 200) {
            message = message.substring(0, 200);
        }
        // #region agent log
        DebugLog.write("H-B", "GoogleOAuthDebugListener:onAuthFailure",
                "Authentication failure event",
                "{\"failureType\":\"" + type + "\""
                        + ",\"errorMessage\":\"" + (message == null ? "" : message.replace("\"", "'").replace("\n", " ")) + "\"}");
        // #endregion
    }
}
