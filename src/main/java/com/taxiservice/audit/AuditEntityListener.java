package com.taxiservice.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxiservice.entity.AuditLog;
import com.taxiservice.repository.AuditLogRepository;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JPA Entity Listener that automatically creates audit log entries
 * for entities annotated with @Auditable.
 *
 * This listener intercepts INSERT, UPDATE, and DELETE operations
 * and stores the changes in the audit_log table.
 */
@Slf4j
@Component
public class AuditEntityListener {

    private static AuditLogRepository auditLogRepository;
    private static ObjectMapper objectMapper;

    @Autowired
    public void init(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        AuditEntityListener.auditLogRepository = auditLogRepository;
        AuditEntityListener.objectMapper = objectMapper;
    }

    /**
     * Called after an entity is inserted
     */
    @PostPersist
    public void onPostPersist(Object entity) {
        if (!isAuditable(entity)) {
            return;
        }

        try {
            String tableName = getTableName(entity);
            Long recordId = getEntityId(entity);
            String actionBy = getCurrentUsername();
            String newData = serializeEntity(entity);

            AuditLog auditLog = AuditLog.builder()
                    .tableName(tableName)
                    .recordId(recordId)
                    .actionType("INSERT")
                    .actionBy(actionBy)
                    .actionAt(LocalDateTime.now())
                    .oldData(null)
                    .newData(newData)
                    .build();

            saveAuditLog(auditLog);
            log.debug("Audit log created for INSERT on {} with ID {}", tableName, recordId);

        } catch (Exception e) {
            log.error("Failed to create audit log for INSERT: {}", e.getMessage(), e);
        }
    }

    /**
     * Called after an entity is updated
     */
    @PostUpdate
    public void onPostUpdate(Object entity) {
        if (!isAuditable(entity)) {
            return;
        }

        try {
            String tableName = getTableName(entity);
            Long recordId = getEntityId(entity);
            String actionBy = getCurrentUsername();
            String newData = serializeEntity(entity);

            // Note: We can't easily get old data in @PostUpdate without custom tracking
            // For now, we'll just log the new state
            AuditLog auditLog = AuditLog.builder()
                    .tableName(tableName)
                    .recordId(recordId)
                    .actionType("UPDATE")
                    .actionBy(actionBy)
                    .actionAt(LocalDateTime.now())
                    .oldData(null) // Could be enhanced with @PreUpdate tracking
                    .newData(newData)
                    .build();

            saveAuditLog(auditLog);
            log.debug("Audit log created for UPDATE on {} with ID {}", tableName, recordId);

        } catch (Exception e) {
            log.error("Failed to create audit log for UPDATE: {}", e.getMessage(), e);
        }
    }

    /**
     * Called before an entity is deleted
     */
    @PreRemove
    public void onPreRemove(Object entity) {
        if (!isAuditable(entity)) {
            return;
        }

        try {
            String tableName = getTableName(entity);
            Long recordId = getEntityId(entity);
            String actionBy = getCurrentUsername();
            String oldData = serializeEntity(entity);

            AuditLog auditLog = AuditLog.builder()
                    .tableName(tableName)
                    .recordId(recordId)
                    .actionType("DELETE")
                    .actionBy(actionBy)
                    .actionAt(LocalDateTime.now())
                    .oldData(oldData)
                    .newData(null)
                    .build();

            saveAuditLog(auditLog);
            log.debug("Audit log created for DELETE on {} with ID {}", tableName, recordId);

        } catch (Exception e) {
            log.error("Failed to create audit log for DELETE: {}", e.getMessage(), e);
        }
    }

    /**
     * Check if entity is annotated with @Auditable
     */
    private boolean isAuditable(Object entity) {
        return entity.getClass().isAnnotationPresent(Auditable.class);
    }

    /**
     * Get table name from @Table annotation or entity class name
     */
    private String getTableName(Object entity) {
        Table tableAnnotation = entity.getClass().getAnnotation(Table.class);
        if (tableAnnotation != null && !tableAnnotation.name().isEmpty()) {
            return tableAnnotation.name();
        }
        return entity.getClass().getSimpleName().toLowerCase();
    }

    /**
     * Extract entity ID using reflection
     */
    private Long getEntityId(Object entity) {
        try {
            // Find field with @Id annotation
            for (Field field : entity.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    if (value instanceof Long) {
                        return (Long) value;
                    } else if (value != null) {
                        return Long.valueOf(value.toString());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to extract entity ID: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Serialize entity to JSON
     */
    private String serializeEntity(Object entity) {
        try {
            // Create a map of field names to values (excluding collections to avoid lazy loading issues)
            Map<String, Object> data = new HashMap<>();
            for (Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();

                // Skip collections and complex relationships to avoid lazy loading
                if (java.util.Collection.class.isAssignableFrom(field.getType()) ||
                    field.isAnnotationPresent(OneToMany.class) ||
                    field.isAnnotationPresent(ManyToMany.class)) {
                    continue;
                }

                // Skip transient fields
                if (field.isAnnotationPresent(Transient.class)) {
                    continue;
                }

                try {
                    Object value = field.get(entity);
                    data.put(fieldName, value);
                } catch (Exception e) {
                    log.debug("Could not access field {}: {}", fieldName, e.getMessage());
                }
            }
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize entity to JSON: {}", e.getMessage());
            return "{}";
        }
    }

    /**
     * Get current authenticated username
     */
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.debug("Could not get current username: {}", e.getMessage());
        }
        return "system";
    }

    /**
     * Save audit log to database
     */
    private void saveAuditLog(AuditLog auditLog) {
        try {
            if (auditLogRepository != null) {
                auditLogRepository.save(auditLog);
            } else {
                log.warn("AuditLogRepository not initialized, cannot save audit log");
            }
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage(), e);
        }
    }
}
