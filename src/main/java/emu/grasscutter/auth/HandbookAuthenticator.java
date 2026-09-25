package emu.grasscutter.auth;

import emu.grasscutter.auth.AuthenticationSystem.AuthenticationRequest;
import lombok.*;

/** Handles player authentication for the web GM handbook. */
public interface HandbookAuthenticator {
    @Getter
    @Builder
    class Response {
        private final int status;
        private final String body;
        @Builder.Default private boolean html = false;
    }

    void presentPage(AuthenticationRequest request);

    Response authenticate(AuthenticationRequest request);
}
