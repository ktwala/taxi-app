package com.taxiservice.repository;

import com.taxiservice.entity.TaxiServiceAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaxiServiceAuditLogRepository extends JpaRepository<TaxiServiceAuditLog, Long> {

    List<TaxiServiceAuditLog> findByTableName(String tableName);

    List<TaxiServiceAuditLog> findByTableNameAndRecordId(String tableName, Long recordId);

    List<TaxiServiceAuditLog> findByActionBy(String actionBy);

    List<TaxiServiceAuditLog> findByActionType(String actionType);

    @Query("SELECT a FROM TaxiServiceAuditLog a WHERE a.actionTimestamp BETWEEN :startDate AND :endDate")
    List<TaxiServiceAuditLog> findByActionTimestampBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM TaxiServiceAuditLog a WHERE a.tableName = :tableName AND a.recordId = :recordId ORDER BY a.actionTimestamp DESC")
    List<TaxiServiceAuditLog> findAuditHistoryForRecord(
            @Param("tableName") String tableName,
            @Param("recordId") Long recordId);

    @Query("SELECT a FROM TaxiServiceAuditLog a WHERE a.actionBy = :actionBy AND a.actionTimestamp BETWEEN :startDate AND :endDate")
    List<TaxiServiceAuditLog> findByActionByAndDateRange(
            @Param("actionBy") String actionBy,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
