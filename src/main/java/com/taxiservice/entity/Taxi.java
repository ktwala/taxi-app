package com.taxiservice.entity;

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
@Table(name = "taxis", indexes = {
        @Index(name = "idx_taxi_license_plate", columnList = "licensePlate", unique = true),
        @Index(name = "idx_taxi_driver_id", columnList = "driverId"),
        @Index(name = "idx_taxi_route_id", columnList = "routeId"),
        @Index(name = "idx_taxi_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Taxi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taxiId;

    @NotBlank(message = "License plate is required")
    @Column(nullable = false, unique = true, length = 20)
    private String licensePlate;

    @NotBlank(message = "Model is required")
    @Column(nullable = false, length = 100)
    private String model;

    @NotBlank(message = "Manufacturer is required")
    @Column(nullable = false, length = 100)
    private String manufacturer;

    @NotNull(message = "Year is required")
    @Positive(message = "Year must be positive")
    @Column(nullable = false)
    private Integer year;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    @Column(nullable = false)
    private Integer capacity;

    @Column(length = 50)
    private String color;

    @NotBlank(message = "Status is required")
    @Column(nullable = false, length = 20)
    private String status; // AVAILABLE, ASSIGNED, MAINTENANCE, OUT_OF_SERVICE

    @Column(name = "driverId")
    private Long driverId;

    @Column(name = "routeId")
    private Long routeId;

    @Column(length = 20)
    private String fuelType; // PETROL, DIESEL, ELECTRIC, HYBRID

    @Column(length = 30)
    private String vehicleType; // SEDAN, SUV, VAN, MINIBUS

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
