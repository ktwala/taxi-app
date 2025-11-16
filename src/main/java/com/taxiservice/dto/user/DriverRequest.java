package com.taxiservice.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    private String contactNumber;
}
