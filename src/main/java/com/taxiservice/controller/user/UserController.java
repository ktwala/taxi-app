package com.taxiservice.controller.user;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.user.UserRequest;
import com.taxiservice.dto.user.UserResponse;
import com.taxiservice.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest request) {
        log.info("REST request to create user: {}", request.getUsername());
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", response));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequest request) {
        log.info("REST request to update user with ID: {}", userId);
        UserResponse response = userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        log.info("REST request to delete user with ID: {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    @PatchMapping("/{userId}/activate")
    public ResponseEntity<ApiResponse<UserResponse>> activateUser(@PathVariable Long userId) {
        log.info("REST request to activate user with ID: {}", userId);
        UserResponse response = userService.activateUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User activated successfully", response));
    }

    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<ApiResponse<UserResponse>> deactivateUser(@PathVariable Long userId) {
        log.info("REST request to deactivate user with ID: {}", userId);
        UserResponse response = userService.deactivateUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully", response));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        log.info("REST request to get user with ID: {}", userId);
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", response));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@PathVariable String username) {
        log.info("REST request to get user by username: {}", username);
        UserResponse response = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        log.info("REST request to get all users");
        List<UserResponse> responses = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", responses));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getActiveUsers() {
        log.info("REST request to get active users");
        List<UserResponse> responses = userService.getActiveUsers();
        return ResponseEntity.ok(ApiResponse.success("Active users retrieved successfully", responses));
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(@PathVariable Long roleId) {
        log.info("REST request to get users by role ID: {}", roleId);
        List<UserResponse> responses = userService.getUsersByRole(roleId);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", responses));
    }
}
