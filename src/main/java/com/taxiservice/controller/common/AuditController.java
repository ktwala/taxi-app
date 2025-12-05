package com.taxiservice.controller.common;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.common.AuditLogResponse;
import com.taxiservice.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'CHAIRPERSON')")
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/table/{tableName}")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogsByTable(@PathVariable String tableName) {
        log.info("REST request to get audit logs for table: {}", tableName);
        List<AuditLogResponse> responses = auditService.getAuditLogsByTable(tableName);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", responses));
    }

    @GetMapping("/table/{tableName}/record/{recordId}")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogsForRecord(
            @PathVariable String tableName,
            @PathVariable Long recordId) {
        log.info("REST request to get audit logs for table: {} and record ID: {}", tableName, recordId);
        List<AuditLogResponse> responses = auditService.getAuditLogsForRecord(tableName, recordId);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", responses));
    }

    @GetMapping("/history/{tableName}/{recordId}")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditHistory(
            @PathVariable String tableName,
            @PathVariable Long recordId) {
        log.info("REST request to get audit history for table: {} and record ID: {}", tableName, recordId);
        List<AuditLogResponse> responses = auditService.getAuditHistory(tableName, recordId);
        return ResponseEntity.ok(ApiResponse.success("Audit history retrieved successfully", responses));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogsByUser(@PathVariable String username) {
        log.info("REST request to get audit logs by user: {}", username);
        List<AuditLogResponse> responses = auditService.getAuditLogsByUser(username);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", responses));
    }

    @GetMapping("/action-type/{actionType}")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogsByActionType(@PathVariable String actionType) {
        log.info("REST request to get audit logs by action type: {}", actionType);
        List<AuditLogResponse> responses = auditService.getAuditLogsByActionType(actionType);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", responses));
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("REST request to get audit logs between {} and {}", startDate, endDate);
        List<AuditLogResponse> responses = auditService.getAuditLogsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", responses));
    }

    @GetMapping("/user-date-range")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogsByUserAndDateRange(
            @RequestParam String username,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("REST request to get audit logs by user: {} between {} and {}", username, startDate, endDate);
        List<AuditLogResponse> responses = auditService.getAuditLogsByUserAndDateRange(username, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", responses));
    }
}
