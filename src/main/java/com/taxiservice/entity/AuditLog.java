package com.taxiservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    @Column(name = "table_name", nullable = false, length = 100)
    private String tableName;

    @Column(name = "record_id", nullable = false)
    private Long recordId;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    @Column(name = "action_by", nullable = false, length = 100)
    private String actionBy;

    @Column(name = "action_at")
    private LocalDateTime actionAt = LocalDateTime.now();

    @Type(JsonBinaryType.class)
    @Column(name = "old_data", columnDefinition = "jsonb")
    private String oldData;

    @Type(JsonBinaryType.class)
    @Column(name = "new_data", columnDefinition = "jsonb")
    private String newData;
}
