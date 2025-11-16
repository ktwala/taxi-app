package com.taxiservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleResponse {

    private Long roleId;
    private String roleName;
    private String permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
