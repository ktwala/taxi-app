package com.taxiservice.entity;

import com.taxiservice.audit.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "taxi")
@EntityListeners({AuditingEntityListener.class, com.taxiservice.audit.AuditEntityListener.class})
@Auditable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Taxi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taxi_id")
    private Long taxiId;

    @NotBlank(message = "Plate number is required")
    @Column(name = "plate_number", nullable = false, unique = true, length = 20)
    private String plateNumber;

    @Column(length = 100)
    private String model;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "route_id")
    private Long routeId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
