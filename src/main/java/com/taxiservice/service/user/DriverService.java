package com.taxiservice.service.user;

import com.taxiservice.dto.user.DriverRequest;
import com.taxiservice.dto.user.DriverResponse;
import com.taxiservice.entity.Driver;
import com.taxiservice.exception.DuplicateResourceException;
import com.taxiservice.exception.ResourceNotFoundException;
import com.taxiservice.repository.DriverRepository;
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
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverResponse createDriver(DriverRequest request) {
        log.info("Creating driver: {}", request.getName());

        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("Driver", "licenseNumber", request.getLicenseNumber());
        }

        Driver driver = Driver.builder()
                .name(request.getName())
                .licenseNumber(request.getLicenseNumber())
                .contactNumber(request.getContactNumber())
                .build();

        Driver saved = driverRepository.save(driver);
        log.info("Driver created successfully with ID: {}", saved.getDriverId());

        return convertToResponse(saved);
    }

    public DriverResponse updateDriver(Long driverId, DriverRequest request) {
        log.info("Updating driver with ID: {}", driverId);

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "driverId", driverId));

        if (!driver.getLicenseNumber().equals(request.getLicenseNumber()) &&
                driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("Driver", "licenseNumber", request.getLicenseNumber());
        }

        driver.setName(request.getName());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setContactNumber(request.getContactNumber());

        Driver updated = driverRepository.save(driver);
        log.info("Driver updated successfully");

        return convertToResponse(updated);
    }

    public void deleteDriver(Long driverId) {
        log.info("Deleting driver with ID: {}", driverId);

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "driverId", driverId));

        driverRepository.delete(driver);
        log.info("Driver deleted successfully");
    }

    @Transactional(readOnly = true)
    public DriverResponse getDriverById(Long driverId) {
        log.info("Fetching driver with ID: {}", driverId);

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "driverId", driverId));

        return convertToResponse(driver);
    }

    @Transactional(readOnly = true)
    public DriverResponse getDriverByLicenseNumber(String licenseNumber) {
        log.info("Fetching driver by license number: {}", licenseNumber);

        Driver driver = driverRepository.findByLicenseNumber(licenseNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "licenseNumber", licenseNumber));

        return convertToResponse(driver);
    }

    @Transactional(readOnly = true)
    public List<DriverResponse> getAllDrivers() {
        log.info("Fetching all drivers");

        return driverRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private DriverResponse convertToResponse(Driver driver) {
        return DriverResponse.builder()
                .driverId(driver.getDriverId())
                .name(driver.getName())
                .licenseNumber(driver.getLicenseNumber())
                .contactNumber(driver.getContactNumber())
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }
}
