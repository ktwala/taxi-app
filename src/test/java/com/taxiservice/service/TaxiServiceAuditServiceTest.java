package com.taxiservice.service;

import com.taxiservice.dto.AuditLogResponse;
import com.taxiservice.entity.TaxiServiceAuditLog;
import com.taxiservice.repository.TaxiServiceAuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaxiServiceAuditServiceTest {

    @Mock
    private TaxiServiceAuditLogRepository auditLogRepository;

    @InjectMocks
    private TaxiServiceAuditService auditService;

    private TaxiServiceAuditLog auditLog;

    @BeforeEach
    void setUp() {
        auditLog = TaxiServiceAuditLog.builder()
                .id(1L)
                .tableName("taxis")
                .recordId(1L)
                .actionType("INSERT")
                .actionBy("system")
                .actionTimestamp(LocalDateTime.now())
                .oldValue(null)
                .newValue("{\"taxiId\":1,\"licensePlate\":\"TX-001-ABC\"}")
                .changes("New record created")
                .build();
    }

    @Test
    void getAuditLogsByTableName_ShouldReturnAuditLogs() {
        // Given
        List<TaxiServiceAuditLog> auditLogs = Arrays.asList(auditLog);
        when(auditLogRepository.findByTableName("taxis")).thenReturn(auditLogs);

        // When
        List<AuditLogResponse> result = auditService.getAuditLogsByTableName("taxis");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("taxis", result.get(0).getTableName());
        verify(auditLogRepository, times(1)).findByTableName("taxis");
    }

    @Test
    void getAuditLogsForRecord_ShouldReturnAuditLogs() {
        // Given
        List<TaxiServiceAuditLog> auditLogs = Arrays.asList(auditLog);
        when(auditLogRepository.findByTableNameAndRecordId("taxis", 1L)).thenReturn(auditLogs);

        // When
        List<AuditLogResponse> result = auditService.getAuditLogsForRecord("taxis", 1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getRecordId());
        verify(auditLogRepository, times(1)).findByTableNameAndRecordId("taxis", 1L);
    }

    @Test
    void getAuditLogsByUser_ShouldReturnAuditLogs() {
        // Given
        List<TaxiServiceAuditLog> auditLogs = Arrays.asList(auditLog);
        when(auditLogRepository.findByActionBy("system")).thenReturn(auditLogs);

        // When
        List<AuditLogResponse> result = auditService.getAuditLogsByUser("system");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("system", result.get(0).getActionBy());
        verify(auditLogRepository, times(1)).findByActionBy("system");
    }

    @Test
    void getAuditLogsByActionType_ShouldReturnAuditLogs() {
        // Given
        List<TaxiServiceAuditLog> auditLogs = Arrays.asList(auditLog);
        when(auditLogRepository.findByActionType("INSERT")).thenReturn(auditLogs);

        // When
        List<AuditLogResponse> result = auditService.getAuditLogsByActionType("INSERT");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("INSERT", result.get(0).getActionType());
        verify(auditLogRepository, times(1)).findByActionType("INSERT");
    }

    @Test
    void getAuditLogsByDateRange_ShouldReturnAuditLogs() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<TaxiServiceAuditLog> auditLogs = Arrays.asList(auditLog);
        when(auditLogRepository.findByActionTimestampBetween(startDate, endDate)).thenReturn(auditLogs);

        // When
        List<AuditLogResponse> result = auditService.getAuditLogsByDateRange(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(auditLogRepository, times(1)).findByActionTimestampBetween(startDate, endDate);
    }

    @Test
    void getAuditHistory_ShouldReturnAuditHistory() {
        // Given
        List<TaxiServiceAuditLog> auditLogs = Arrays.asList(auditLog);
        when(auditLogRepository.findAuditHistoryForRecord("taxis", 1L)).thenReturn(auditLogs);

        // When
        List<AuditLogResponse> result = auditService.getAuditHistory("taxis", 1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("taxis", result.get(0).getTableName());
        assertEquals(1L, result.get(0).getRecordId());
        verify(auditLogRepository, times(1)).findAuditHistoryForRecord("taxis", 1L);
    }
}
