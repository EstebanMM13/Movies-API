package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.error.dto.ResponseError;
import com.estebanmmk13.movies.models.User;
import com.estebanmmk13.movies.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET ALL USERS
    @Operation(
            summary = "Get all users",
            description = "Retrieve a paginated list of all users"
    )
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<User>> findAllUsers(
            @Parameter(description = "Pagination information")
            Pageable pageable) {

        return ResponseEntity.ok(userService.findAllUsers(pageable));
    }


    // GET USER BY ID
    @Operation(summary = "Get user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> findUserById(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long id) {

        return ResponseEntity.ok(userService.findUserById(id));
    }


    // CREATE USER
    @Operation(summary = "Create a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content(schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping
    public ResponseEntity<User> createUser(
            @Parameter(description = "User object to create", required = true)
            @Valid @RequestBody User user) {

        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }


    // UPDATE USER
    @Operation(summary = "Update an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "User fields to update", required = true)
            @Valid @RequestBody User user) {

        return ResponseEntity.ok(userService.updateUser(id, user));
    }


    // DELETE USER
    @Operation(summary = "Delete a user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long id) {

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    // FIND BY USERNAME
    @Operation(
            summary = "Find user by username",
            description = "Retrieve a user by username (case insensitive)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<User> findUserByUsername(
            @Parameter(description = "Username", required = true)
            @PathVariable String username) {

        return ResponseEntity.ok(
                userService.findUserByUsernameIgnoreCase(username)
        );
    }


    // FIND BY EMAIL
    @Operation(summary = "Find user by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<User> findUserByEmail(
            @Parameter(description = "Email", required = true)
            @PathVariable String email) {

        return ResponseEntity.ok(userService.findUserByEmail(email));
    }


    // EXISTS BY EMAIL
    @Operation(
            summary = "Check if user exists by email",
            description = "Returns true if a user with the given email exists"
    )
    @ApiResponse(responseCode = "200", description = "Existence checked successfully")
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> existsUserByEmail(
            @Parameter(description = "Email to check existence", required = true)
            @PathVariable String email) {

        return ResponseEntity.ok(
                userService.existsUserByEmail(email)
        );
    }
}