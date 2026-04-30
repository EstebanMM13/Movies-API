package com.estebanmmk13.movies.services.auth;

import com.estebanmmk13.movies.dtoModels.response.AuthResponse;
import com.estebanmmk13.movies.dtoModels.request.AuthenticationRequest;
import com.estebanmmk13.movies.dtoModels.request.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse authenticate(AuthenticationRequest authenticationRequest);
}
