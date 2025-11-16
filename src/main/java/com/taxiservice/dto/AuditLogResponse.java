package com.taxiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {

    private Long id;
    private String tableName;
    private Long recordId;
    private String actionType;
    private String actionBy;
    private LocalDateTime actionTimestamp;
    private String oldValue;
    private String newValue;
    private String changes;
    private String ipAddress;
    private String userAgent;
}
