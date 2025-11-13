package com.taxiservice.service.user;

import com.taxiservice.dto.user.UserRequest;
import com.taxiservice.dto.user.UserResponse;
import com.taxiservice.entity.User;
import com.taxiservice.entity.UserRole;
import com.taxiservice.exception.DuplicateResourceException;
import com.taxiservice.exception.ResourceNotFoundException;
import com.taxiservice.repository.UserRepository;
import com.taxiservice.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    // Note: In production, inject PasswordEncoder from Spring Security
    // private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserRequest request) {
        log.info("Creating user: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        if (request.getContactEmail() != null && userRepository.existsByContactEmail(request.getContactEmail())) {
            throw new DuplicateResourceException("User", "contactEmail", request.getContactEmail());
        }

        // Verify role exists
        userRoleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("UserRole", "roleId", request.getRoleId()));

        User user = User.builder()
                .username(request.getUsername())
                // In production: .passwordHash(passwordEncoder.encode(request.getPassword()))
                .passwordHash(request.getPassword()) // Temporary - should be hashed
                .fullName(request.getFullName())
                .contactEmail(request.getContactEmail())
                .roleId(request.getRoleId())
                .active(request.getActive())
                .build();

        User saved = userRepository.save(user);
        log.info("User created successfully with ID: {}", saved.getUserId());

        return convertToResponse(saved);
    }

    public UserResponse updateUser(Long userId, UserRequest request) {
        log.info("Updating user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }

        if (request.getContactEmail() != null &&
                !request.getContactEmail().equals(user.getContactEmail()) &&
                userRepository.existsByContactEmail(request.getContactEmail())) {
            throw new DuplicateResourceException("User", "contactEmail", request.getContactEmail());
        }

        // Verify role exists
        userRoleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("UserRole", "roleId", request.getRoleId()));

        user.setUsername(request.getUsername());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            // In production: user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setPasswordHash(request.getPassword());
        }
        user.setFullName(request.getFullName());
        user.setContactEmail(request.getContactEmail());
        user.setRoleId(request.getRoleId());
        user.setActive(request.getActive());

        User updated = userRepository.save(user);
        log.info("User updated successfully");

        return convertToResponse(updated);
    }

    public void deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        userRepository.delete(user);
        log.info("User deleted successfully");
    }

    public UserResponse activateUser(Long userId) {
        log.info("Activating user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        user.setActive(true);
        User updated = userRepository.save(user);

        return convertToResponse(updated);
    }

    public UserResponse deactivateUser(Long userId) {
        log.info("Deactivating user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        user.setActive(false);
        User updated = userRepository.save(user);

        return convertToResponse(updated);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        log.info("Fetching user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        return convertToResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return convertToResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");

        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getActiveUsers() {
        log.info("Fetching active users");

        return userRepository.findAllActiveUsers().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(Long roleId) {
        log.info("Fetching users by role ID: {}", roleId);

        return userRepository.findByRoleId(roleId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .contactEmail(user.getContactEmail())
                .roleId(user.getRoleId())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        // Fetch role name if available
        if (user.getUserRole() != null) {
            response.setRoleName(user.getUserRole().getRoleName());
        } else {
            userRoleRepository.findById(user.getRoleId())
                    .ifPresent(role -> response.setRoleName(role.getRoleName()));
        }

        return response;
    }
}
