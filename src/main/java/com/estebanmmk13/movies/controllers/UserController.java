package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@CrossOrigin
@Tag(name = "Users", description = "Operations related to user management")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Get all users",
            description = "Retrieve a paginated list of all users"
    )
    @GetMapping
    public Page<User> findAllUsers(
            @Parameter(description = "Pagination information") Pageable pageable) {
        return userService.findAllUsers(pageable);
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<User> findUserById(
            @Parameter(description = "ID of the user to retrieve") @PathVariable Long id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Create a new user")
    @PostMapping
    public ResponseEntity<User> createUser(
            @Parameter(description = "User object to create") @Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @Operation(summary = "Update an existing user")
    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "ID of the user to update") @PathVariable Long id,
            @Parameter(description = "User fields to update") @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete a user by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Find user by username",
            description = "Retrieve a user by username (case insensitive)"
    )
    @GetMapping("/username/{username}")
    public ResponseEntity<User> findUserByUsername(
            @Parameter(description = "Username of the user") @PathVariable String username) {
        User user = userService.findUserByUsernameIgnoreCase(username);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Find user by email")
    @GetMapping("/email/{email}")
    public ResponseEntity<User> findUserByEmail(
            @Parameter(description = "Email of the user") @PathVariable String email) {
        User user = userService.findUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Check if user exists by email",
            description = "Returns true if a user with the given email exists"
    )
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> existsUserByEmail(
            @Parameter(description = "Email to check existence") @PathVariable String email) {
        boolean exists = userService.existsUserByEmail(email);
        return ResponseEntity.ok(exists);
    }
}