package com.taxiservice.controller;

import com.taxiservice.dto.ApiResponse;
import com.taxiservice.dto.TaxiRequest;
import com.taxiservice.dto.TaxiResponse;
import com.taxiservice.service.TaxiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taxis")
@RequiredArgsConstructor
@Slf4j
public class TaxiController {

    private final TaxiService taxiService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaxiResponse>>> getAllTaxis() {
        log.info("REST request to get all taxis");
        List<TaxiResponse> taxis = taxiService.getAllTaxis();
        return ResponseEntity.ok(ApiResponse.success("Taxis retrieved successfully", taxis));
    }

    @GetMapping("/{taxiId}")
    public ResponseEntity<ApiResponse<TaxiResponse>> getTaxiById(@PathVariable Long taxiId) {
        log.info("REST request to get taxi with ID: {}", taxiId);
        TaxiResponse taxi = taxiService.getTaxiById(taxiId);
        return ResponseEntity.ok(ApiResponse.success("Taxi retrieved successfully", taxi));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse<List<TaxiResponse>>> getTaxisByDriverId(@PathVariable Long driverId) {
        log.info("REST request to get taxis for driver ID: {}", driverId);
        List<TaxiResponse> taxis = taxiService.getTaxisByDriverId(driverId);
        return ResponseEntity.ok(ApiResponse.success("Taxis retrieved successfully", taxis));
    }

    @GetMapping("/route/{routeId}")
    public ResponseEntity<ApiResponse<List<TaxiResponse>>> getTaxisByRouteId(@PathVariable Long routeId) {
        log.info("REST request to get taxis for route ID: {}", routeId);
        List<TaxiResponse> taxis = taxiService.getTaxisByRouteId(routeId);
        return ResponseEntity.ok(ApiResponse.success("Taxis retrieved successfully", taxis));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<TaxiResponse>>> getAvailableTaxis() {
        log.info("REST request to get available taxis");
        List<TaxiResponse> taxis = taxiService.getAvailableTaxis();
        return ResponseEntity.ok(ApiResponse.success("Available taxis retrieved successfully", taxis));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaxiResponse>> createTaxi(@Valid @RequestBody TaxiRequest request) {
        log.info("REST request to create new taxi");
        TaxiResponse taxi = taxiService.createTaxi(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Taxi created successfully", taxi));
    }

    @PutMapping("/{taxiId}")
    public ResponseEntity<ApiResponse<TaxiResponse>> updateTaxi(
            @PathVariable Long taxiId,
            @Valid @RequestBody TaxiRequest request) {
        log.info("REST request to update taxi with ID: {}", taxiId);
        TaxiResponse taxi = taxiService.updateTaxi(taxiId, request);
        return ResponseEntity.ok(ApiResponse.success("Taxi updated successfully", taxi));
    }

    @DeleteMapping("/{taxiId}")
    public ResponseEntity<ApiResponse<Void>> deleteTaxi(@PathVariable Long taxiId) {
        log.info("REST request to delete taxi with ID: {}", taxiId);
        taxiService.deleteTaxi(taxiId);
        return ResponseEntity.ok(ApiResponse.success("Taxi deleted successfully", null));
    }

    @PutMapping("/{taxiId}/assign-driver/{driverId}")
    public ResponseEntity<ApiResponse<TaxiResponse>> assignDriverToTaxi(
            @PathVariable Long taxiId,
            @PathVariable Long driverId) {
        log.info("REST request to assign driver {} to taxi {}", driverId, taxiId);
        TaxiResponse taxi = taxiService.assignDriverToTaxi(taxiId, driverId);
        return ResponseEntity.ok(ApiResponse.success("Driver assigned successfully", taxi));
    }

    @PutMapping("/{taxiId}/assign-route/{routeId}")
    public ResponseEntity<ApiResponse<TaxiResponse>> assignRouteToTaxi(
            @PathVariable Long taxiId,
            @PathVariable Long routeId) {
        log.info("REST request to assign route {} to taxi {}", routeId, taxiId);
        TaxiResponse taxi = taxiService.assignRouteToTaxi(taxiId, routeId);
        return ResponseEntity.ok(ApiResponse.success("Route assigned successfully", taxi));
    }
}
