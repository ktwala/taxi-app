package com.taxiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponse {

    private Long routeId;
    private String routeName;
    private String startLocation;
    private String endLocation;
    private Double distance;
    private String status;
}
