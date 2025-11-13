package com.taxiservice.service.user;

import com.taxiservice.dto.user.RouteRequest;
import com.taxiservice.dto.user.RouteResponse;
import com.taxiservice.entity.Route;
import com.taxiservice.exception.ResourceNotFoundException;
import com.taxiservice.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RouteService {

    private final RouteRepository routeRepository;

    public RouteResponse createRoute(RouteRequest request) {
        log.info("Creating route: {}", request.getName());

        Route route = Route.builder()
                .name(request.getName())
                .startPoint(request.getStartPoint())
                .endPoint(request.getEndPoint())
                .isActive(request.getIsActive())
                .build();

        Route saved = routeRepository.save(route);
        log.info("Route created successfully with ID: {}", saved.getRouteId());

        return convertToResponse(saved);
    }

    public RouteResponse updateRoute(Long routeId, RouteRequest request) {
        log.info("Updating route with ID: {}", routeId);

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "routeId", routeId));

        route.setName(request.getName());
        route.setStartPoint(request.getStartPoint());
        route.setEndPoint(request.getEndPoint());
        route.setIsActive(request.getIsActive());

        Route updated = routeRepository.save(route);
        log.info("Route updated successfully");

        return convertToResponse(updated);
    }

    public void deleteRoute(Long routeId) {
        log.info("Deleting route with ID: {}", routeId);

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "routeId", routeId));

        routeRepository.delete(route);
        log.info("Route deleted successfully");
    }

    public RouteResponse activateRoute(Long routeId) {
        log.info("Activating route with ID: {}", routeId);

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "routeId", routeId));

        route.setIsActive(true);
        Route updated = routeRepository.save(route);

        return convertToResponse(updated);
    }

    public RouteResponse deactivateRoute(Long routeId) {
        log.info("Deactivating route with ID: {}", routeId);

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "routeId", routeId));

        route.setIsActive(false);
        Route updated = routeRepository.save(route);

        return convertToResponse(updated);
    }

    @Transactional(readOnly = true)
    public RouteResponse getRouteById(Long routeId) {
        log.info("Fetching route with ID: {}", routeId);

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "routeId", routeId));

        return convertToResponse(route);
    }

    @Transactional(readOnly = true)
    public List<RouteResponse> getAllRoutes() {
        log.info("Fetching all routes");

        return routeRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RouteResponse> getActiveRoutes() {
        log.info("Fetching active routes");

        return routeRepository.findAllActiveRoutes().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RouteResponse> searchRoutes(String name) {
        log.info("Searching routes by name: {}", name);

        return routeRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private RouteResponse convertToResponse(Route route) {
        return RouteResponse.builder()
                .routeId(route.getRouteId())
                .name(route.getName())
                .startPoint(route.getStartPoint())
                .endPoint(route.getEndPoint())
                .isActive(route.getIsActive())
                .createdAt(route.getCreatedAt())
                .updatedAt(route.getUpdatedAt())
                .build();
    }
}
