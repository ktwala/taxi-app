package com.taxiservice.controller.common;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.common.TaxiRequest;
import com.taxiservice.dto.common.TaxiResponse;
import com.taxiservice.service.TaxiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taxis")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY')")
public class TaxiController {

    private final TaxiService taxiService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaxiResponse>> createTaxi(@Valid @RequestBody TaxiRequest request) {
        log.info("REST request to create taxi with plate number: {}", request.getPlateNumber());
        TaxiResponse response = taxiService.createTaxi(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Taxi created successfully", response));
    }

    @PutMapping("/{taxiId}")
    public ResponseEntity<ApiResponse<TaxiResponse>> updateTaxi(
            @PathVariable Long taxiId,
            @Valid @RequestBody TaxiRequest request) {
        log.info("REST request to update taxi with ID: {}", taxiId);
        TaxiResponse response = taxiService.updateTaxi(taxiId, request);
        return ResponseEntity.ok(ApiResponse.success("Taxi updated successfully", response));
    }

    @DeleteMapping("/{taxiId}")
    public ResponseEntity<ApiResponse<Void>> deleteTaxi(@PathVariable Long taxiId) {
        log.info("REST request to delete taxi with ID: {}", taxiId);
        taxiService.deleteTaxi(taxiId);
        return ResponseEntity.ok(ApiResponse.success("Taxi deleted successfully", null));
    }

    @PatchMapping("/{taxiId}/assign-driver/{driverId}")
    public ResponseEntity<ApiResponse<TaxiResponse>> assignDriver(
            @PathVariable Long taxiId,
            @PathVariable Long driverId) {
        log.info("REST request to assign driver ID: {} to taxi ID: {}", driverId, taxiId);
        TaxiResponse response = taxiService.assignDriverToTaxi(taxiId, driverId);
        return ResponseEntity.ok(ApiResponse.success("Driver assigned successfully", response));
    }

    // Alternative endpoint for driver assignment (supports PUT and different path format)
    @PutMapping("/{taxiId}/driver/{driverId}")
    public ResponseEntity<ApiResponse<TaxiResponse>> assignDriverAlt(
            @PathVariable Long taxiId,
            @PathVariable Long driverId) {
        log.info("REST request to assign driver ID: {} to taxi ID: {} (alt endpoint)", driverId, taxiId);
        TaxiResponse response = taxiService.assignDriverToTaxi(taxiId, driverId);
        return ResponseEntity.ok(ApiResponse.success("Driver assigned successfully", response));
    }

    @PatchMapping("/{taxiId}/assign-route/{routeId}")
    public ResponseEntity<ApiResponse<TaxiResponse>> assignRoute(
            @PathVariable Long taxiId,
            @PathVariable Long routeId) {
        log.info("REST request to assign route ID: {} to taxi ID: {}", routeId, taxiId);
        TaxiResponse response = taxiService.assignRouteToTaxi(taxiId, routeId);
        return ResponseEntity.ok(ApiResponse.success("Route assigned successfully", response));
    }

    // Alternative endpoint for route assignment (supports PUT and different path format)
    @PutMapping("/{taxiId}/route/{routeId}")
    public ResponseEntity<ApiResponse<TaxiResponse>> assignRouteAlt(
            @PathVariable Long taxiId,
            @PathVariable Long routeId) {
        log.info("REST request to assign route ID: {} to taxi ID: {} (alt endpoint)", routeId, taxiId);
        TaxiResponse response = taxiService.assignRouteToTaxi(taxiId, routeId);
        return ResponseEntity.ok(ApiResponse.success("Route assigned successfully", response));
    }

    @GetMapping("/{taxiId}")
    public ResponseEntity<ApiResponse<TaxiResponse>> getTaxiById(@PathVariable Long taxiId) {
        log.info("REST request to get taxi with ID: {}", taxiId);
        TaxiResponse response = taxiService.getTaxiById(taxiId);
        return ResponseEntity.ok(ApiResponse.success("Taxi retrieved successfully", response));
    }

    @GetMapping("/plate/{plateNumber}")
    public ResponseEntity<ApiResponse<TaxiResponse>> getTaxiByPlateNumber(@PathVariable String plateNumber) {
        log.info("REST request to get taxi by plate number: {}", plateNumber);
        TaxiResponse response = taxiService.getTaxiByPlateNumber(plateNumber);
        return ResponseEntity.ok(ApiResponse.success("Taxi retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaxiResponse>>> getAllTaxis() {
        log.info("REST request to get all taxis");
        List<TaxiResponse> responses = taxiService.getAllTaxis();
        return ResponseEntity.ok(ApiResponse.success("Taxis retrieved successfully", responses));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse<List<TaxiResponse>>> getTaxisByDriver(@PathVariable Long driverId) {
        log.info("REST request to get taxis for driver ID: {}", driverId);
        List<TaxiResponse> responses = taxiService.getTaxisByDriverId(driverId);
        return ResponseEntity.ok(ApiResponse.success("Taxis retrieved successfully", responses));
    }

    @GetMapping("/route/{routeId}")
    public ResponseEntity<ApiResponse<List<TaxiResponse>>> getTaxisByRoute(@PathVariable Long routeId) {
        log.info("REST request to get taxis for route ID: {}", routeId);
        List<TaxiResponse> responses = taxiService.getTaxisByRouteId(routeId);
        return ResponseEntity.ok(ApiResponse.success("Taxis retrieved successfully", responses));
    }

    @GetMapping("/unassigned")
    public ResponseEntity<ApiResponse<List<TaxiResponse>>> getUnassignedTaxis() {
        log.info("REST request to get unassigned taxis");
        List<TaxiResponse> responses = taxiService.getAvailableTaxis();
        return ResponseEntity.ok(ApiResponse.success("Unassigned taxis retrieved", responses));
    }
}
