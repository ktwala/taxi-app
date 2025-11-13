package com.taxiservice.dto.common;

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
    private String plateNumber;
    private String model;
    private Long driverId;
    private String driverName;
    private Long routeId;
    private String routeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
