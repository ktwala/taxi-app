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
public class RouteRequest {

    @NotBlank(message = "Route name is required")
    private String name;

    @NotBlank(message = "Start point is required")
    private String startPoint;

    @NotBlank(message = "End point is required")
    private String endPoint;

    private Boolean isActive = true;
}
