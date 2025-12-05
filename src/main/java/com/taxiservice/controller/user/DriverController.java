package com.taxiservice.controller.user;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.user.DriverRequest;
import com.taxiservice.dto.user.DriverResponse;
import com.taxiservice.service.user.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY')")
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<ApiResponse<DriverResponse>> createDriver(@Valid @RequestBody DriverRequest request) {
        log.info("REST request to create driver: {}", request.getName());
        DriverResponse response = driverService.createDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Driver created successfully", response));
    }

    @PutMapping("/{driverId}")
    public ResponseEntity<ApiResponse<DriverResponse>> updateDriver(
            @PathVariable Long driverId,
            @Valid @RequestBody DriverRequest request) {
        log.info("REST request to update driver with ID: {}", driverId);
        DriverResponse response = driverService.updateDriver(driverId, request);
        return ResponseEntity.ok(ApiResponse.success("Driver updated successfully", response));
    }

    @DeleteMapping("/{driverId}")
    public ResponseEntity<ApiResponse<Void>> deleteDriver(@PathVariable Long driverId) {
        log.info("REST request to delete driver with ID: {}", driverId);
        driverService.deleteDriver(driverId);
        return ResponseEntity.ok(ApiResponse.success("Driver deleted successfully", null));
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<ApiResponse<DriverResponse>> getDriverById(@PathVariable Long driverId) {
        log.info("REST request to get driver with ID: {}", driverId);
        DriverResponse response = driverService.getDriverById(driverId);
        return ResponseEntity.ok(ApiResponse.success("Driver retrieved successfully", response));
    }

    @GetMapping("/license/{licenseNumber}")
    public ResponseEntity<ApiResponse<DriverResponse>> getDriverByLicenseNumber(@PathVariable String licenseNumber) {
        log.info("REST request to get driver by license number: {}", licenseNumber);
        DriverResponse response = driverService.getDriverByLicenseNumber(licenseNumber);
        return ResponseEntity.ok(ApiResponse.success("Driver retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DriverResponse>>> getAllDrivers() {
        log.info("REST request to get all drivers");
        List<DriverResponse> responses = driverService.getAllDrivers();
        return ResponseEntity.ok(ApiResponse.success("Drivers retrieved successfully", responses));
    }
}
