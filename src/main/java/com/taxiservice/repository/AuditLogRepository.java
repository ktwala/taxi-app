package com.taxiservice.repository;

import com.taxiservice.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByTableName(String tableName);

    List<AuditLog> findByRecordId(Long recordId);

    List<AuditLog> findByTableNameAndRecordId(String tableName, Long recordId);

    List<AuditLog> findByActionType(String actionType);

    List<AuditLog> findByActionBy(String actionBy);

    @Query("SELECT a FROM AuditLog a WHERE a.actionAt BETWEEN :startDate AND :endDate")
    List<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM AuditLog a WHERE a.tableName = :tableName AND a.recordId = :recordId ORDER BY a.actionAt DESC")
    List<AuditLog> findAuditHistory(@Param("tableName") String tableName, @Param("recordId") Long recordId);

    @Query("SELECT a FROM AuditLog a WHERE a.actionBy = :actionBy AND a.actionAt BETWEEN :startDate AND :endDate")
    List<AuditLog> findByUserAndDateRange(@Param("actionBy") String actionBy,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
}
