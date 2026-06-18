package com.pakbzer.security;

import com.pakbzer.service.UserService;
import com.pakbzer.config.DebugLog;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

/**
 * Hooks into Google OIDC login: after Google authenticates the user we make
 * sure a matching PakBzer account exists in our own database.
 */
@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserService userService;

    public CustomOidcUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // #region agent log
        DebugLog.write("H-C", "CustomOidcUserService:loadUser", "OIDC loadUser called",
                "{\"registrationId\":\"" + userRequest.getClientRegistration().getRegistrationId() + "\"}");
        // #endregion
        try {
            OidcUser oidcUser = super.loadUser(userRequest);

            String email = oidcUser.getEmail();
            String name = oidcUser.getFullName() != null ? oidcUser.getFullName() : email;
            boolean emailPresent = email != null && !email.isBlank();
            // #region agent log
            DebugLog.write("H-C", "CustomOidcUserService:loadUser", "OIDC userinfo received",
                    "{\"emailPresent\":" + emailPresent
                            + ",\"emailVerified\":" + Boolean.TRUE.equals(oidcUser.getEmailVerified()) + "}");
            // #endregion
            if (email != null) {
                userService.findOrCreateGoogleUser(email, name);
                // #region agent log
                DebugLog.write("H-C", "CustomOidcUserService:loadUser", "Google user provisioned",
                        "{\"dbStep\":\"ok\"}");
                // #endregion
            }
            return oidcUser;
        } catch (Exception ex) {
            // #region agent log
            DebugLog.write("H-C", "CustomOidcUserService:loadUser", "OIDC loadUser failed",
                    "{\"errorType\":\"" + ex.getClass().getSimpleName()
                            + "\",\"errorMessage\":\"" + (ex.getMessage() == null ? "" : ex.getMessage().replace("\"", "'").replace("\n", " ")) + "\"}");
            // #endregion
            throw ex;
        }
    }
}
