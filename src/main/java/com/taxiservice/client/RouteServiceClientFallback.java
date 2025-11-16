package com.taxiservice.client;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.RouteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RouteServiceClientFallback implements RouteServiceClient {

    @Override
    public ApiResponse<RouteResponse> getRouteById(Long routeId) {
        log.error("Fallback: Unable to fetch route with ID: {}", routeId);
        return ApiResponse.error("Route service is currently unavailable");
    }
}
