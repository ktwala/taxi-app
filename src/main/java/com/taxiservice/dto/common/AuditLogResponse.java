package com.taxiservice.dto.common;

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

    private Long auditId;
    private String tableName;
    private Long recordId;
    private String actionType;
    private String actionBy;
    private LocalDateTime actionAt;
    private String oldData; // JSON string
    private String newData; // JSON string
}
