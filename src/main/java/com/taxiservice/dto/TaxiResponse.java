package com.taxiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxiResponse {

    private Long taxiId;
    private String licensePlate;
    private String model;
    private String manufacturer;
    private Integer year;
    private Integer capacity;
    private String color;
    private String status;
    private Long driverId;
    private String driverName;
    private Long routeId;
    private String routeName;
    private String fuelType;
    private String vehicleType;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
