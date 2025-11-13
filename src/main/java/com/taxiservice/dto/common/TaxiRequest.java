package com.taxiservice.dto.common;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxiRequest {

    @NotBlank(message = "Plate number is required")
    private String plateNumber;

    private String model;

    private Long driverId;

    private Long routeId;
}
