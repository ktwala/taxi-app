package com.taxiservice.service;

import com.taxiservice.dto.common.TaxiRequest;
import com.taxiservice.dto.common.TaxiResponse;
import com.taxiservice.entity.Taxi;
import com.taxiservice.exception.DuplicateResourceException;
import com.taxiservice.exception.ResourceNotFoundException;
import com.taxiservice.repository.DriverRepository;
import com.taxiservice.repository.RouteRepository;
import com.taxiservice.repository.TaxiRepository;
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
public class TaxiServiceUpdated {

    private final TaxiRepository taxiRepository;
    private final DriverRepository driverRepository;
    private final RouteRepository routeRepository;

    public TaxiResponse createTaxi(TaxiRequest request) {
        log.info("Creating taxi with plate number: {}", request.getPlateNumber());

        if (taxiRepository.existsByPlateNumber(request.getPlateNumber())) {
            throw new DuplicateResourceException("Taxi", "plateNumber", request.getPlateNumber());
        }

        // Validate driver if provided
        if (request.getDriverId() != null) {
            driverRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver", "driverId", request.getDriverId()));
        }

        // Validate route if provided
        if (request.getRouteId() != null) {
            routeRepository.findById(request.getRouteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Route", "routeId", request.getRouteId()));
        }

        Taxi taxi = Taxi.builder()
                .plateNumber(request.getPlateNumber())
                .model(request.getModel())
                .driverId(request.getDriverId())
                .routeId(request.getRouteId())
                .build();

        Taxi saved = taxiRepository.save(taxi);
        log.info("Taxi created successfully with ID: {}", saved.getTaxiId());

        return convertToResponse(saved);
    }

    public TaxiResponse updateTaxi(Long taxiId, TaxiRequest request) {
        log.info("Updating taxi with ID: {}", taxiId);

        Taxi taxi = taxiRepository.findById(taxiId)
                .orElseThrow(() -> new ResourceNotFoundException("Taxi", "taxiId", taxiId));

        if (!taxi.getPlateNumber().equals(request.getPlateNumber()) &&
                taxiRepository.existsByPlateNumber(request.getPlateNumber())) {
            throw new DuplicateResourceException("Taxi", "plateNumber", request.getPlateNumber());
        }

        // Validate driver if provided
        if (request.getDriverId() != null) {
            driverRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver", "driverId", request.getDriverId()));
        }

        // Validate route if provided
        if (request.getRouteId() != null) {
            routeRepository.findById(request.getRouteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Route", "routeId", request.getRouteId()));
        }

        taxi.setPlateNumber(request.getPlateNumber());
        taxi.setModel(request.getModel());
        taxi.setDriverId(request.getDriverId());
        taxi.setRouteId(request.getRouteId());

        Taxi updated = taxiRepository.save(taxi);
        log.info("Taxi updated successfully");

        return convertToResponse(updated);
    }

    public void deleteTaxi(Long taxiId) {
        log.info("Deleting taxi with ID: {}", taxiId);

        Taxi taxi = taxiRepository.findById(taxiId)
                .orElseThrow(() -> new ResourceNotFoundException("Taxi", "taxiId", taxiId));

        taxiRepository.delete(taxi);
        log.info("Taxi deleted successfully");
    }

    public TaxiResponse assignDriver(Long taxiId, Long driverId) {
        log.info("Assigning driver ID: {} to taxi ID: {}", driverId, taxiId);

        Taxi taxi = taxiRepository.findById(taxiId)
                .orElseThrow(() -> new ResourceNotFoundException("Taxi", "taxiId", taxiId));

        // Verify driver exists
        driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "driverId", driverId));

        taxi.setDriverId(driverId);

        Taxi updated = taxiRepository.save(taxi);
        log.info("Driver assigned successfully");

        return convertToResponse(updated);
    }

    public TaxiResponse assignRoute(Long taxiId, Long routeId) {
        log.info("Assigning route ID: {} to taxi ID: {}", routeId, taxiId);

        Taxi taxi = taxiRepository.findById(taxiId)
                .orElseThrow(() -> new ResourceNotFoundException("Taxi", "taxiId", taxiId));

        // Verify route exists
        routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "routeId", routeId));

        taxi.setRouteId(routeId);

        Taxi updated = taxiRepository.save(taxi);
        log.info("Route assigned successfully");

        return convertToResponse(updated);
    }

    @Transactional(readOnly = true)
    public TaxiResponse getTaxiById(Long taxiId) {
        log.info("Fetching taxi with ID: {}", taxiId);

        Taxi taxi = taxiRepository.findById(taxiId)
                .orElseThrow(() -> new ResourceNotFoundException("Taxi", "taxiId", taxiId));

        return convertToResponse(taxi);
    }

    @Transactional(readOnly = true)
    public TaxiResponse getTaxiByPlateNumber(String plateNumber) {
        log.info("Fetching taxi by plate number: {}", plateNumber);

        Taxi taxi = taxiRepository.findByPlateNumber(plateNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Taxi", "plateNumber", plateNumber));

        return convertToResponse(taxi);
    }

    @Transactional(readOnly = true)
    public List<TaxiResponse> getAllTaxis() {
        log.info("Fetching all taxis");

        return taxiRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaxiResponse> getTaxisByDriver(Long driverId) {
        log.info("Fetching taxis for driver ID: {}", driverId);

        return taxiRepository.findByDriverId(driverId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaxiResponse> getTaxisByRoute(Long routeId) {
        log.info("Fetching taxis for route ID: {}", routeId);

        return taxiRepository.findByRouteId(routeId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaxiResponse> getUnassignedTaxis() {
        log.info("Fetching unassigned taxis");

        return taxiRepository.findUnassignedTaxis().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private TaxiResponse convertToResponse(Taxi taxi) {
        TaxiResponse response = TaxiResponse.builder()
                .taxiId(taxi.getTaxiId())
                .plateNumber(taxi.getPlateNumber())
                .model(taxi.getModel())
                .driverId(taxi.getDriverId())
                .routeId(taxi.getRouteId())
                .createdAt(taxi.getCreatedAt())
                .updatedAt(taxi.getUpdatedAt())
                .build();

        // Fetch driver name if available
        if (taxi.getDriverId() != null) {
            driverRepository.findById(taxi.getDriverId())
                    .ifPresent(driver -> response.setDriverName(driver.getName()));
        }

        // Fetch route name if available
        if (taxi.getRouteId() != null) {
            routeRepository.findById(taxi.getRouteId())
                    .ifPresent(route -> response.setRouteName(route.getName()));
        }

        return response;
    }
}
