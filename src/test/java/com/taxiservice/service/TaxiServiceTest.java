package com.taxiservice.service;

import com.taxiservice.client.DriverServiceClient;
import com.taxiservice.client.RouteServiceClient;
import com.taxiservice.dto.common.*;
import com.taxiservice.dto.DriverResponse;
import com.taxiservice.dto.RouteResponse;
import com.taxiservice.entity.Taxi;
import com.taxiservice.exception.DuplicateResourceException;
import com.taxiservice.exception.ResourceNotFoundException;
import com.taxiservice.repository.TaxiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaxiServiceTest {

    @Mock
    private TaxiRepository taxiRepository;

    @Mock
    private DriverServiceClient driverServiceClient;

    @Mock
    private RouteServiceClient routeServiceClient;

    @InjectMocks
    private TaxiService taxiService;

    private Taxi taxi;
    private TaxiRequest taxiRequest;

    @BeforeEach
    void setUp() {
        taxi = Taxi.builder()
                .taxiId(1L)
                .plateNumber("TX-001-ABC")
                .model("Camry")
                .driverId(null)
                .routeId(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        taxiRequest = TaxiRequest.builder()
                .plateNumber("TX-001-ABC")
                .model("Camry")
                .driverId(null)
                .routeId(null)
                .build();
    }

    @Test
    void getAllTaxis_ShouldReturnAllTaxis() {
        // Given
        List<Taxi> taxis = Arrays.asList(taxi);
        when(taxiRepository.findAll()).thenReturn(taxis);

        // When
        List<TaxiResponse> result = taxiService.getAllTaxis();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TX-001-ABC", result.get(0).getPlateNumber());
        verify(taxiRepository, times(1)).findAll();
    }

    @Test
    void getTaxiById_WhenExists_ShouldReturnTaxi() {
        // Given
        when(taxiRepository.findById(1L)).thenReturn(Optional.of(taxi));

        // When
        TaxiResponse result = taxiService.getTaxiById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getTaxiId());
        assertEquals("TX-001-ABC", result.getPlateNumber());
        verify(taxiRepository, times(1)).findById(1L);
    }

    @Test
    void getTaxiById_WhenNotExists_ShouldThrowException() {
        // Given
        when(taxiRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> taxiService.getTaxiById(1L));
        verify(taxiRepository, times(1)).findById(1L);
    }

    @Test
    void createTaxi_WhenValidRequest_ShouldCreateTaxi() {
        // Given
        when(taxiRepository.existsByPlateNumber(anyString())).thenReturn(false);
        when(taxiRepository.save(any(Taxi.class))).thenReturn(taxi);

        // When
        TaxiResponse result = taxiService.createTaxi(taxiRequest);

        // Then
        assertNotNull(result);
        assertEquals("TX-001-ABC", result.getPlateNumber());
        verify(taxiRepository, times(1)).existsByPlateNumber(anyString());
        verify(taxiRepository, times(1)).save(any(Taxi.class));
    }

    @Test
    void createTaxi_WhenDuplicatePlateNumber_ShouldThrowException() {
        // Given
        when(taxiRepository.existsByPlateNumber(anyString())).thenReturn(true);

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> taxiService.createTaxi(taxiRequest));
        verify(taxiRepository, times(1)).existsByPlateNumber(anyString());
        verify(taxiRepository, never()).save(any(Taxi.class));
    }

    @Test
    void updateTaxi_WhenExists_ShouldUpdateTaxi() {
        // Given
        when(taxiRepository.findById(1L)).thenReturn(Optional.of(taxi));
        when(taxiRepository.existsByPlateNumber(anyString())).thenReturn(false);
        when(taxiRepository.save(any(Taxi.class))).thenReturn(taxi);

        // When
        TaxiResponse result = taxiService.updateTaxi(1L, taxiRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getTaxiId());
        verify(taxiRepository, times(1)).findById(1L);
        verify(taxiRepository, times(1)).save(any(Taxi.class));
    }

    @Test
    void updateTaxi_WhenNotExists_ShouldThrowException() {
        // Given
        when(taxiRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> taxiService.updateTaxi(1L, taxiRequest));
        verify(taxiRepository, times(1)).findById(1L);
        verify(taxiRepository, never()).save(any(Taxi.class));
    }

    @Test
    void deleteTaxi_WhenExists_ShouldDeleteTaxi() {
        // Given
        when(taxiRepository.findById(1L)).thenReturn(Optional.of(taxi));
        doNothing().when(taxiRepository).delete(any(Taxi.class));

        // When
        taxiService.deleteTaxi(1L);

        // Then
        verify(taxiRepository, times(1)).findById(1L);
        verify(taxiRepository, times(1)).delete(any(Taxi.class));
    }

    @Test
    void deleteTaxi_WhenNotExists_ShouldThrowException() {
        // Given
        when(taxiRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> taxiService.deleteTaxi(1L));
        verify(taxiRepository, times(1)).findById(1L);
        verify(taxiRepository, never()).delete(any(Taxi.class));
    }

    @Test
    void getTaxisByDriverId_ShouldReturnTaxis() {
        // Given
        List<Taxi> taxis = Arrays.asList(taxi);
        when(taxiRepository.findByDriverId(1L)).thenReturn(taxis);

        // When
        List<TaxiResponse> result = taxiService.getTaxisByDriverId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taxiRepository, times(1)).findByDriverId(1L);
    }

    @Test
    void getTaxisByRouteId_ShouldReturnTaxis() {
        // Given
        List<Taxi> taxis = Arrays.asList(taxi);
        when(taxiRepository.findByRouteId(1L)).thenReturn(taxis);

        // When
        List<TaxiResponse> result = taxiService.getTaxisByRouteId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taxiRepository, times(1)).findByRouteId(1L);
    }

    @Test
    void getAvailableTaxis_ShouldReturnAvailableTaxis() {
        // Given
        List<Taxi> taxis = Arrays.asList(taxi);
        when(taxiRepository.findUnassignedTaxis()).thenReturn(taxis);

        // When
        List<TaxiResponse> result = taxiService.getAvailableTaxis();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getDriverId());
        verify(taxiRepository, times(1)).findUnassignedTaxis();
    }

    @Test
    void assignDriverToTaxi_WhenValidDriver_ShouldAssignDriver() {
        // Given
        DriverResponse driverResponse = DriverResponse.builder()
                .driverId(1L)
                .firstName("John")
                .lastName("Doe")
                .build();
        ApiResponse<DriverResponse> apiResponse = ApiResponse.success("Driver retrieved successfully", driverResponse);

        when(taxiRepository.findById(1L)).thenReturn(Optional.of(taxi));
        when(driverServiceClient.getDriverById(1L)).thenReturn(apiResponse);
        when(taxiRepository.save(any(Taxi.class))).thenReturn(taxi);

        // When
        TaxiResponse result = taxiService.assignDriverToTaxi(1L, 1L);

        // Then
        assertNotNull(result);
        verify(taxiRepository, times(1)).findById(1L);
        verify(driverServiceClient, times(1)).getDriverById(1L);
        verify(taxiRepository, times(1)).save(any(Taxi.class));
    }

    @Test
    void assignRouteToTaxi_WhenValidRoute_ShouldAssignRoute() {
        // Given
        RouteResponse routeResponse = RouteResponse.builder()
                .routeId(1L)
                .routeName("Route A")
                .build();
        ApiResponse<RouteResponse> apiResponse = ApiResponse.success("Route retrieved successfully", routeResponse);

        when(taxiRepository.findById(1L)).thenReturn(Optional.of(taxi));
        when(routeServiceClient.getRouteById(1L)).thenReturn(apiResponse);
        when(taxiRepository.save(any(Taxi.class))).thenReturn(taxi);

        // When
        TaxiResponse result = taxiService.assignRouteToTaxi(1L, 1L);

        // Then
        assertNotNull(result);
        verify(taxiRepository, times(1)).findById(1L);
        verify(routeServiceClient, times(1)).getRouteById(1L);
        verify(taxiRepository, times(1)).save(any(Taxi.class));
    }
}
