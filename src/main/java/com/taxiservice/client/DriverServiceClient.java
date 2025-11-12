package com.taxiservice.client;

import com.taxiservice.dto.ApiResponse;
import com.taxiservice.dto.DriverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "driver-service",
        url = "${feign.client.config.driver-service.url}",
        fallback = DriverServiceClientFallback.class
)
public interface DriverServiceClient {

    @GetMapping("/api/drivers/{driverId}")
    ApiResponse<DriverResponse> getDriverById(@PathVariable("driverId") Long driverId);
}
