package com.taxiservice.audit;

import java.lang.annotation.*;

/**
 * Annotation to mark entities that should be audited.
 * When applied to an entity, all INSERT, UPDATE, and DELETE operations
 * will be automatically logged to the audit_log table.
 *
 * Usage:
 * @Entity
 * @Auditable
 * public class MyEntity { ... }
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {
}
