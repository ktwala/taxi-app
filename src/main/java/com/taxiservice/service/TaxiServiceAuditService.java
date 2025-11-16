package com.taxiservice.service;

import com.taxiservice.dto.common.AuditLogResponse;
import com.taxiservice.entity.TaxiServiceAuditLog;
import com.taxiservice.repository.TaxiServiceAuditLogRepository;
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
public class TaxiServiceAuditService {

    private final TaxiServiceAuditLogRepository auditLogRepository;

    public List<AuditLogResponse> getAuditLogsByTableName(String tableName) {
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

    public List<AuditLogResponse> getAuditLogsByUser(String actionBy) {
        log.info("Fetching audit logs for user: {}", actionBy);
        return auditLogRepository.findByActionBy(actionBy).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<AuditLogResponse> getAuditLogsByActionType(String actionType) {
        log.info("Fetching audit logs for action type: {}", actionType);
        return auditLogRepository.findByActionType(actionType).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<AuditLogResponse> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching audit logs between {} and {}", startDate, endDate);
        return auditLogRepository.findByActionTimestampBetween(startDate, endDate).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<AuditLogResponse> getAuditHistory(String tableName, Long recordId) {
        log.info("Fetching complete audit history for table: {} and record ID: {}", tableName, recordId);
        return auditLogRepository.findAuditHistoryForRecord(tableName, recordId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private AuditLogResponse convertToResponse(TaxiServiceAuditLog auditLog) {
        return AuditLogResponse.builder()
                .auditId(auditLog.getId())
                .tableName(auditLog.getTableName())
                .recordId(auditLog.getRecordId())
                .actionType(auditLog.getActionType())
                .actionBy(auditLog.getActionBy())
                .actionAt(auditLog.getActionTimestamp())
                .oldData(auditLog.getOldValue())
                .newData(auditLog.getNewValue())
                .build();
    }
}
