package com.taxiservice.client;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.user.DriverResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DriverServiceClientFallback implements DriverServiceClient {

    @Override
    public ApiResponse<DriverResponse> getDriverById(Long driverId) {
        log.error("Fallback: Unable to fetch driver with ID: {}", driverId);
        return ApiResponse.error("Driver service is currently unavailable");
    }
}
