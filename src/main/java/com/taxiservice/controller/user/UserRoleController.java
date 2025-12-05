package com.taxiservice.controller.user;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.user.UserRoleRequest;
import com.taxiservice.dto.user.UserRoleResponse;
import com.taxiservice.service.user.UserRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-roles")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserRoleResponse>> createRole(@Valid @RequestBody UserRoleRequest request) {
        log.info("REST request to create user role: {}", request.getRoleName());
        UserRoleResponse response = userRoleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User role created successfully", response));
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<ApiResponse<UserRoleResponse>> updateRole(
            @PathVariable Long roleId,
            @Valid @RequestBody UserRoleRequest request) {
        log.info("REST request to update user role with ID: {}", roleId);
        UserRoleResponse response = userRoleService.updateRole(roleId, request);
        return ResponseEntity.ok(ApiResponse.success("User role updated successfully", response));
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long roleId) {
        log.info("REST request to delete user role with ID: {}", roleId);
        userRoleService.deleteRole(roleId);
        return ResponseEntity.ok(ApiResponse.success("User role deleted successfully", null));
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<ApiResponse<UserRoleResponse>> getRoleById(@PathVariable Long roleId) {
        log.info("REST request to get user role with ID: {}", roleId);
        UserRoleResponse response = userRoleService.getRoleById(roleId);
        return ResponseEntity.ok(ApiResponse.success("User role retrieved successfully", response));
    }

    @GetMapping("/name/{roleName}")
    public ResponseEntity<ApiResponse<UserRoleResponse>> getRoleByName(@PathVariable String roleName) {
        log.info("REST request to get user role by name: {}", roleName);
        UserRoleResponse response = userRoleService.getRoleByName(roleName);
        return ResponseEntity.ok(ApiResponse.success("User role retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserRoleResponse>>> getAllRoles() {
        log.info("REST request to get all user roles");
        List<UserRoleResponse> responses = userRoleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success("User roles retrieved successfully", responses));
    }
}
