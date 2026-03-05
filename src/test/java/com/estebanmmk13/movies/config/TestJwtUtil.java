package com.estebanmmk13.movies.config;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestJwtUtil {

    @Autowired
    private JwtService jwtService;

    public String createTestToken(String username) {
        User testUser = User.builder()
                .username(username)
                .email(username + "@test.com")
                .password("password")
                .role(Role.USER)
                .build();

        return jwtService.generateToken(testUser);
    }

    public String createTestTokenWithRole(String username, Role role) {
        User testUser = User.builder()
                .username(username)
                .email(username + "@test.com")
                .password("password")
                .role(role)
                .build();

        return jwtService.generateToken(testUser);
    }
}