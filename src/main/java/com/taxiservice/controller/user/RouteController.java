package com.taxiservice.controller.user;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.user.RouteRequest;
import com.taxiservice.dto.user.RouteResponse;
import com.taxiservice.service.user.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@Slf4j
public class RouteController {

    private final RouteService routeService;

    @PostMapping
    public ResponseEntity<ApiResponse<RouteResponse>> createRoute(@Valid @RequestBody RouteRequest request) {
        log.info("REST request to create route: {}", request.getName());
        RouteResponse response = routeService.createRoute(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Route created successfully", response));
    }

    @PutMapping("/{routeId}")
    public ResponseEntity<ApiResponse<RouteResponse>> updateRoute(
            @PathVariable Long routeId,
            @Valid @RequestBody RouteRequest request) {
        log.info("REST request to update route with ID: {}", routeId);
        RouteResponse response = routeService.updateRoute(routeId, request);
        return ResponseEntity.ok(ApiResponse.success("Route updated successfully", response));
    }

    @DeleteMapping("/{routeId}")
    public ResponseEntity<ApiResponse<Void>> deleteRoute(@PathVariable Long routeId) {
        log.info("REST request to delete route with ID: {}", routeId);
        routeService.deleteRoute(routeId);
        return ResponseEntity.ok(ApiResponse.success("Route deleted successfully", null));
    }

    @PatchMapping("/{routeId}/activate")
    public ResponseEntity<ApiResponse<RouteResponse>> activateRoute(@PathVariable Long routeId) {
        log.info("REST request to activate route with ID: {}", routeId);
        RouteResponse response = routeService.activateRoute(routeId);
        return ResponseEntity.ok(ApiResponse.success("Route activated successfully", response));
    }

    @PatchMapping("/{routeId}/deactivate")
    public ResponseEntity<ApiResponse<RouteResponse>> deactivateRoute(@PathVariable Long routeId) {
        log.info("REST request to deactivate route with ID: {}", routeId);
        RouteResponse response = routeService.deactivateRoute(routeId);
        return ResponseEntity.ok(ApiResponse.success("Route deactivated successfully", response));
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<ApiResponse<RouteResponse>> getRouteById(@PathVariable Long routeId) {
        log.info("REST request to get route with ID: {}", routeId);
        RouteResponse response = routeService.getRouteById(routeId);
        return ResponseEntity.ok(ApiResponse.success("Route retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RouteResponse>>> getAllRoutes() {
        log.info("REST request to get all routes");
        List<RouteResponse> responses = routeService.getAllRoutes();
        return ResponseEntity.ok(ApiResponse.success("Routes retrieved successfully", responses));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<RouteResponse>>> getActiveRoutes() {
        log.info("REST request to get active routes");
        List<RouteResponse> responses = routeService.getActiveRoutes();
        return ResponseEntity.ok(ApiResponse.success("Active routes retrieved successfully", responses));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<RouteResponse>>> searchRoutes(@RequestParam String keyword) {
        log.info("REST request to search routes with keyword: {}", keyword);
        List<RouteResponse> responses = routeService.searchRoutes(keyword);
        return ResponseEntity.ok(ApiResponse.success("Routes retrieved successfully", responses));
    }
}
