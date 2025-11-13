package com.taxiservice.service.user;

import com.taxiservice.dto.user.UserRoleRequest;
import com.taxiservice.dto.user.UserRoleResponse;
import com.taxiservice.entity.UserRole;
import com.taxiservice.exception.DuplicateResourceException;
import com.taxiservice.exception.ResourceNotFoundException;
import com.taxiservice.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRoleResponse createRole(UserRoleRequest request) {
        log.info("Creating user role: {}", request.getRoleName());

        if (userRoleRepository.existsByRoleName(request.getRoleName())) {
            throw new DuplicateResourceException("UserRole", "roleName", request.getRoleName());
        }

        UserRole role = UserRole.builder()
                .roleName(request.getRoleName())
                .permissions(request.getPermissions())
                .build();

        UserRole saved = userRoleRepository.save(role);
        log.info("User role created successfully with ID: {}", saved.getRoleId());

        return convertToResponse(saved);
    }

    public UserRoleResponse updateRole(Long roleId, UserRoleRequest request) {
        log.info("Updating user role with ID: {}", roleId);

        UserRole role = userRoleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole", "roleId", roleId));

        if (!role.getRoleName().equals(request.getRoleName()) &&
                userRoleRepository.existsByRoleName(request.getRoleName())) {
            throw new DuplicateResourceException("UserRole", "roleName", request.getRoleName());
        }

        role.setRoleName(request.getRoleName());
        role.setPermissions(request.getPermissions());

        UserRole updated = userRoleRepository.save(role);
        log.info("User role updated successfully");

        return convertToResponse(updated);
    }

    public void deleteRole(Long roleId) {
        log.info("Deleting user role with ID: {}", roleId);

        UserRole role = userRoleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole", "roleId", roleId));

        userRoleRepository.delete(role);
        log.info("User role deleted successfully");
    }

    @Transactional(readOnly = true)
    public UserRoleResponse getRoleById(Long roleId) {
        log.info("Fetching user role with ID: {}", roleId);

        UserRole role = userRoleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole", "roleId", roleId));

        return convertToResponse(role);
    }

    @Transactional(readOnly = true)
    public UserRoleResponse getRoleByName(String roleName) {
        log.info("Fetching user role by name: {}", roleName);

        UserRole role = userRoleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole", "roleName", roleName));

        return convertToResponse(role);
    }

    @Transactional(readOnly = true)
    public List<UserRoleResponse> getAllRoles() {
        log.info("Fetching all user roles");

        return userRoleRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private UserRoleResponse convertToResponse(UserRole role) {
        return UserRoleResponse.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .permissions(role.getPermissions())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
