package com.estebanmmk13.movies.controllers;

import com.estebanmmk13.movies.dtoModels.response.UserResponseDTO;
import com.estebanmmk13.movies.dtoModels.response.SystemStats;
import com.estebanmmk13.movies.models.Role;
import com.estebanmmk13.movies.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Endpoints exclusivos para administradores")
@SecurityRequirement(name = "bearer-auth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    @Operation(summary = "Obtener todos los usuarios con paginación (solo ADMIN)")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        log.info("Admin - Obteniendo usuarios - página: {}, tamaño: {}", page, size);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        return ResponseEntity.ok(userService.findAllUsers(pageable));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Obtener usuario por ID (solo ADMIN)")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.info("Admin - Obteniendo usuario con ID: {}", id);
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Eliminar usuario (solo ADMIN)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Admin - Eliminando usuario con ID: {}", id);
        userService.deleteUser(id);
        log.info("Usuario eliminado exitosamente: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @Operation(summary = "Estadísticas del sistema (solo ADMIN)")
    public ResponseEntity<SystemStats> getSystemStats() {
        log.info("Admin - Obteniendo estadísticas del sistema");

        long totalUsers = userService.countUsers();
        long adminUsers = userService.countUsersByRole(Role.ADMIN);
        long regularUsers = userService.countUsersByRole(Role.USER);

        SystemStats stats = SystemStats.builder()
                .totalUsers(totalUsers)
                .adminUsers(adminUsers)
                .regularUsers(regularUsers)
                .build();

        return ResponseEntity.ok(stats);
    }
}