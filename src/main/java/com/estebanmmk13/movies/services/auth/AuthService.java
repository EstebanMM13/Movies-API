package com.estebanmmk13.movies.services.auth;

import com.estebanmmk13.movies.models.AuthResponse;
import com.estebanmmk13.movies.models.AuthenticationRequest;
import com.estebanmmk13.movies.models.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse authenticate(AuthenticationRequest authenticationRequest);
}
