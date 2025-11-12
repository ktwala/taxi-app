package com.taxiservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "taxi_service_audit_log", indexes = {
        @Index(name = "idx_audit_table_name", columnList = "tableName"),
        @Index(name = "idx_audit_record_id", columnList = "recordId"),
        @Index(name = "idx_audit_action_by", columnList = "actionBy"),
        @Index(name = "idx_audit_action_type", columnList = "actionType"),
        @Index(name = "idx_audit_timestamp", columnList = "actionTimestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxiServiceAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String tableName;

    @Column(nullable = false)
    private Long recordId;

    @Column(nullable = false, length = 20)
    private String actionType; // INSERT, UPDATE, DELETE

    @Column(nullable = false, length = 100)
    private String actionBy;

    @Column(nullable = false)
    private LocalDateTime actionTimestamp;

    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Column(columnDefinition = "TEXT")
    private String newValue;

    @Column(columnDefinition = "TEXT")
    private String changes;

    @Column(length = 50)
    private String ipAddress;

    @Column(length = 200)
    private String userAgent;
}
