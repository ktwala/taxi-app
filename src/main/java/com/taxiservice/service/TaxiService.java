package com.taxiservice.service;

import com.taxiservice.client.DriverServiceClient;
import com.taxiservice.client.RouteServiceClient;
import com.taxiservice.dto.ApiResponse;
import com.taxiservice.dto.DriverResponse;
import com.taxiservice.dto.RouteResponse;
import com.taxiservice.dto.TaxiRequest;
import com.taxiservice.dto.TaxiResponse;
import com.taxiservice.entity.Taxi;
import com.taxiservice.exception.DuplicateResourceException;
import com.taxiservice.exception.ResourceNotFoundException;
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
public class TaxiService {

    private final TaxiRepository taxiRepository;
    private final DriverServiceClient driverServiceClient;
    private final RouteServiceClient routeServiceClient;

    public List<TaxiResponse> getAllTaxis() {
        log.info("Fetching all taxis");
        return taxiRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public TaxiResponse getTaxiById(Long taxiId) {
        log.info("Fetching taxi with ID: {}", taxiId);
        Taxi taxi = taxiRepository.findById(taxiId)
                .orElseThrow(() -> new ResourceNotFoundException("Taxi", "taxiId", taxiId));
        return convertToResponse(taxi);
    }

    public List<TaxiResponse> getTaxisByDriverId(Long driverId) {
        log.info("Fetching taxis for driver ID: {}", driverId);
        return taxiRepository.findByDriverId(driverId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<TaxiResponse> getTaxisByRouteId(Long routeId) {
        log.info("Fetching taxis for route ID: {}", routeId);
        return taxiRepository.findByRouteId(routeId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<TaxiResponse> getAvailableTaxis() {
        log.info("Fetching available taxis");
        return taxiRepository.findAvailableTaxis().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public TaxiResponse createTaxi(TaxiRequest request) {
        log.info("Creating new taxi with license plate: {}", request.getLicensePlate());

        if (taxiRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new DuplicateResourceException("Taxi", "licensePlate", request.getLicensePlate());
        }

        // Validate driver if provided
        if (request.getDriverId() != null) {
            validateDriver(request.getDriverId());
        }

        // Validate route if provided
        if (request.getRouteId() != null) {
            validateRoute(request.getRouteId());
        }

        Taxi taxi = convertToEntity(request);
        Taxi savedTaxi = taxiRepository.save(taxi);
        log.info("Taxi created successfully with ID: {}", savedTaxi.getTaxiId());

        return convertToResponse(savedTaxi);
    }

    public TaxiResponse updateTaxi(Long taxiId, TaxiRequest request) {
        log.info("Updating taxi with ID: {}", taxiId);

        Taxi existingTaxi = taxiRepository.findById(taxiId)
                .orElseThrow(() -> new ResourceNotFoundException("Taxi", "taxiId", taxiId));

        // Check if license plate is being changed and if it already exists
        if (!existingTaxi.getLicensePlate().equals(request.getLicensePlate()) &&
                taxiRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new DuplicateResourceException("Taxi", "licensePlate", request.getLicensePlate());
        }

        // Validate driver if provided
        if (request.getDriverId() != null) {
            validateDriver(request.getDriverId());
        }

        // Validate route if provided
        if (request.getRouteId() != null) {
            validateRoute(request.getRouteId());
        }

        updateTaxiFields(existingTaxi, request);
        Taxi updatedTaxi = taxiRepository.save(existingTaxi);
        log.info("Taxi updated successfully with ID: {}", updatedTaxi.getTaxiId());

        return convertToResponse(updatedTaxi);
    }

    public void deleteTaxi(Long taxiId) {
        log.info("Deleting taxi with ID: {}", taxiId);
        Taxi taxi = taxiRepository.findById(taxiId)
                .orElseThrow(() -> new ResourceNotFoundException("Taxi", "taxiId", taxiId));
        taxiRepository.delete(taxi);
        log.info("Taxi deleted successfully with ID: {}", taxiId);
    }

    public TaxiResponse assignDriverToTaxi(Long taxiId, Long driverId) {
        log.info("Assigning driver {} to taxi {}", driverId, taxiId);

        Taxi taxi = taxiRepository.findById(taxiId)
                .orElseThrow(() -> new ResourceNotFoundException("Taxi", "taxiId", taxiId));

        validateDriver(driverId);
        taxi.setDriverId(driverId);
        Taxi updatedTaxi = taxiRepository.save(taxi);

        log.info("Driver assigned successfully to taxi {}", taxiId);
        return convertToResponse(updatedTaxi);
    }

    public TaxiResponse assignRouteToTaxi(Long taxiId, Long routeId) {
        log.info("Assigning route {} to taxi {}", routeId, taxiId);

        Taxi taxi = taxiRepository.findById(taxiId)
                .orElseThrow(() -> new ResourceNotFoundException("Taxi", "taxiId", taxiId));

        validateRoute(routeId);
        taxi.setRouteId(routeId);
        Taxi updatedTaxi = taxiRepository.save(taxi);

        log.info("Route assigned successfully to taxi {}", taxiId);
        return convertToResponse(updatedTaxi);
    }

    private void validateDriver(Long driverId) {
        try {
            ApiResponse<DriverResponse> response = driverServiceClient.getDriverById(driverId);
            if (!response.isSuccess() || response.getData() == null) {
                throw new ResourceNotFoundException("Driver", "driverId", driverId);
            }
        } catch (Exception e) {
            log.error("Error validating driver: {}", e.getMessage());
            throw new ResourceNotFoundException("Driver", "driverId", driverId);
        }
    }

    private void validateRoute(Long routeId) {
        try {
            ApiResponse<RouteResponse> response = routeServiceClient.getRouteById(routeId);
            if (!response.isSuccess() || response.getData() == null) {
                throw new ResourceNotFoundException("Route", "routeId", routeId);
            }
        } catch (Exception e) {
            log.error("Error validating route: {}", e.getMessage());
            throw new ResourceNotFoundException("Route", "routeId", routeId);
        }
    }

    private Taxi convertToEntity(TaxiRequest request) {
        return Taxi.builder()
                .licensePlate(request.getLicensePlate())
                .model(request.getModel())
                .manufacturer(request.getManufacturer())
                .year(request.getYear())
                .capacity(request.getCapacity())
                .color(request.getColor())
                .status(request.getStatus())
                .driverId(request.getDriverId())
                .routeId(request.getRouteId())
                .fuelType(request.getFuelType())
                .vehicleType(request.getVehicleType())
                .notes(request.getNotes())
                .build();
    }

    private void updateTaxiFields(Taxi taxi, TaxiRequest request) {
        taxi.setLicensePlate(request.getLicensePlate());
        taxi.setModel(request.getModel());
        taxi.setManufacturer(request.getManufacturer());
        taxi.setYear(request.getYear());
        taxi.setCapacity(request.getCapacity());
        taxi.setColor(request.getColor());
        taxi.setStatus(request.getStatus());
        taxi.setDriverId(request.getDriverId());
        taxi.setRouteId(request.getRouteId());
        taxi.setFuelType(request.getFuelType());
        taxi.setVehicleType(request.getVehicleType());
        taxi.setNotes(request.getNotes());
    }

    private TaxiResponse convertToResponse(Taxi taxi) {
        TaxiResponse response = TaxiResponse.builder()
                .taxiId(taxi.getTaxiId())
                .licensePlate(taxi.getLicensePlate())
                .model(taxi.getModel())
                .manufacturer(taxi.getManufacturer())
                .year(taxi.getYear())
                .capacity(taxi.getCapacity())
                .color(taxi.getColor())
                .status(taxi.getStatus())
                .driverId(taxi.getDriverId())
                .routeId(taxi.getRouteId())
                .fuelType(taxi.getFuelType())
                .vehicleType(taxi.getVehicleType())
                .notes(taxi.getNotes())
                .createdAt(taxi.getCreatedAt())
                .updatedAt(taxi.getUpdatedAt())
                .build();

        // Fetch driver name if driverId exists
        if (taxi.getDriverId() != null) {
            try {
                ApiResponse<DriverResponse> driverResponse = driverServiceClient.getDriverById(taxi.getDriverId());
                if (driverResponse.isSuccess() && driverResponse.getData() != null) {
                    DriverResponse driver = driverResponse.getData();
                    response.setDriverName(driver.getFirstName() + " " + driver.getLastName());
                }
            } catch (Exception e) {
                log.warn("Could not fetch driver details for driver ID: {}", taxi.getDriverId());
            }
        }

        // Fetch route name if routeId exists
        if (taxi.getRouteId() != null) {
            try {
                ApiResponse<RouteResponse> routeResponse = routeServiceClient.getRouteById(taxi.getRouteId());
                if (routeResponse.isSuccess() && routeResponse.getData() != null) {
                    RouteResponse route = routeResponse.getData();
                    response.setRouteName(route.getRouteName());
                }
            } catch (Exception e) {
                log.warn("Could not fetch route details for route ID: {}", taxi.getRouteId());
            }
        }

        return response;
    }
}
