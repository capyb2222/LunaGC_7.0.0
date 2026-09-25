package emu.grasscutter.auth;

import emu.grasscutter.server.http.objects.*;

public interface Authenticator<T> {

    T authenticate(AuthenticationSystem.AuthenticationRequest request);
}
