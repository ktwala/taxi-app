package com.taxiservice.service;

import com.taxiservice.dto.common.AuditLogResponse;
import com.taxiservice.entity.AuditLog;
import com.taxiservice.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public List<AuditLogResponse> getAuditLogsByTable(String tableName) {
        log.info("Fetching audit logs for table: {}", tableName);

        return auditLogRepository.findByTableName(tableName).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<AuditLogResponse> getAuditLogsForRecord(String tableName, Long recordId) {
        log.info("Fetching audit logs for table: {} and record ID: {}", tableName, recordId);

        return auditLogRepository.findByTableNameAndRecordId(tableName, recordId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<AuditLogResponse> getAuditHistory(String tableName, Long recordId) {
        log.info("Fetching audit history for table: {} and record ID: {}", tableName, recordId);

        return auditLogRepository.findAuditHistory(tableName, recordId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<AuditLogResponse> getAuditLogsByUser(String username) {
        log.info("Fetching audit logs by user: {}", username);

        return auditLogRepository.findByActionBy(username).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<AuditLogResponse> getAuditLogsByActionType(String actionType) {
        log.info("Fetching audit logs by action type: {}", actionType);

        return auditLogRepository.findByActionType(actionType).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<AuditLogResponse> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching audit logs between {} and {}", startDate, endDate);

        return auditLogRepository.findByDateRange(startDate, endDate).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<AuditLogResponse> getAuditLogsByUserAndDateRange(String username, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching audit logs by user: {} between {} and {}", username, startDate, endDate);

        return auditLogRepository.findByUserAndDateRange(username, startDate, endDate).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private AuditLogResponse convertToResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .auditId(auditLog.getAuditId())
                .tableName(auditLog.getTableName())
                .recordId(auditLog.getRecordId())
                .actionType(auditLog.getActionType())
                .actionBy(auditLog.getActionBy())
                .actionAt(auditLog.getActionAt())
                .oldData(auditLog.getOldData())
                .newData(auditLog.getNewData())
                .build();
    }
}
