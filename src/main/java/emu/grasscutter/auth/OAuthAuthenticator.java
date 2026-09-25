package emu.grasscutter.auth;

import emu.grasscutter.auth.AuthenticationSystem.AuthenticationRequest;

/** Handles authentication via OAuth routes. */
public interface OAuthAuthenticator {

    void handleLogin(AuthenticationRequest request);

    void handleRedirection(AuthenticationRequest request, ClientType clientType);

    void handleTokenProcess(AuthenticationRequest request);

    /** The type of the client. Used for handling redirection. */
    enum ClientType {
        DESKTOP,
        MOBILE
    }
}
