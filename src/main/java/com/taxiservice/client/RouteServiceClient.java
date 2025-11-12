package com.taxiservice.client;

import com.taxiservice.dto.ApiResponse;
import com.taxiservice.dto.RouteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "route-service",
        url = "${feign.client.config.route-service.url}",
        fallback = RouteServiceClientFallback.class
)
public interface RouteServiceClient {

    @GetMapping("/api/routes/{routeId}")
    ApiResponse<RouteResponse> getRouteById(@PathVariable("routeId") Long routeId);
}
