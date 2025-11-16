package com.taxiservice.controller;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.common.AuditLogResponse;
import com.taxiservice.service.TaxiServiceAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/taxis/audit")
@RequiredArgsConstructor
@Slf4j
public class TaxiServiceAuditController {

    private final TaxiServiceAuditService auditService;

    @GetMapping("/table/{tableName}")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogsByTableName(
            @PathVariable String tableName) {
        log.info("REST request to get audit logs for table: {}", tableName);
        List<AuditLogResponse> auditLogs = auditService.getAuditLogsByTableName(tableName);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", auditLogs));
    }

    @GetMapping("/record/{tableName}/{recordId}")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogsForRecord(
            @PathVariable String tableName,
            @PathVariable Long recordId) {
        log.info("REST request to get audit logs for table: {} and record ID: {}", tableName, recordId);
        List<AuditLogResponse> auditLogs = auditService.getAuditLogsForRecord(tableName, recordId);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", auditLogs));
    }

    @GetMapping("/user/{actionBy}")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogsByUser(
            @PathVariable String actionBy) {
        log.info("REST request to get audit logs for user: {}", actionBy);
        List<AuditLogResponse> auditLogs = auditService.getAuditLogsByUser(actionBy);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", auditLogs));
    }

    @GetMapping("/action/{actionType}")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogsByActionType(
            @PathVariable String actionType) {
        log.info("REST request to get audit logs for action type: {}", actionType);
        List<AuditLogResponse> auditLogs = auditService.getAuditLogsByActionType(actionType);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", auditLogs));
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("REST request to get audit logs between {} and {}", startDate, endDate);
        List<AuditLogResponse> auditLogs = auditService.getAuditLogsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", auditLogs));
    }

    @GetMapping("/history/{tableName}/{recordId}")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditHistory(
            @PathVariable String tableName,
            @PathVariable Long recordId) {
        log.info("REST request to get audit history for table: {} and record ID: {}", tableName, recordId);
        List<AuditLogResponse> auditLogs = auditService.getAuditHistory(tableName, recordId);
        return ResponseEntity.ok(ApiResponse.success("Audit history retrieved successfully", auditLogs));
    }
}
