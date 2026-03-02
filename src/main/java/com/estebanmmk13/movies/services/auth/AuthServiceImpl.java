package com.estebanmmk13.movies.services.auth;

import com.estebanmmk13.movies.config.JwtService;
import com.estebanmmk13.movies.error.DuplicateResourceException;
import com.estebanmmk13.movies.error.InvalidCredentialsException;
import com.estebanmmk13.movies.error.ResourceNotFoundException;
import com.estebanmmk13.movies.models.*;
import com.estebanmmk13.movies.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        // 1. Verificar si el email ya existe (para evitar el error 403)
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException(  // ← Tu excepción personalizada
                    "User", "email", registerRequest.getEmail()
            );
        }

        // 2. Verificar si el username ya existe (opcional)
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateResourceException(
                    "User", "username", registerRequest.getUsername()
            );
        }

        var user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder().token(jwtToken).build();
    }

    @Override
    public AuthResponse authenticate(AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    ));
        } catch (BadCredentialsException e) {
            // 3. Capturar error de credenciales y lanzar excepción personalizada
            throw new InvalidCredentialsException("User or password incorrect");
        }

        var user = userRepository.findUserByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(  // ← Excepción personalizada
                        "User", "username", authenticationRequest.getUsername()
                ));

        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder().token(jwtToken).build();
    }
}
