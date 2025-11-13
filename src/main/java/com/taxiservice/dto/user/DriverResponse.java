package com.taxiservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverResponse {

    private Long driverId;
    private String name;
    private String licenseNumber;
    private String contactNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
