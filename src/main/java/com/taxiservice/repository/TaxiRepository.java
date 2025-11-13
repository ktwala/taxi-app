package com.taxiservice.repository;

import com.taxiservice.entity.Taxi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaxiRepository extends JpaRepository<Taxi, Long> {

    Optional<Taxi> findByPlateNumber(String plateNumber);

    List<Taxi> findByDriverId(Long driverId);

    List<Taxi> findByRouteId(Long routeId);

    @Query("SELECT t FROM Taxi t WHERE t.driverId IS NULL")
    List<Taxi> findUnassignedTaxis();

    @Query("SELECT t FROM Taxi t WHERE t.routeId IS NULL")
    List<Taxi> findTaxisWithoutRoute();

    boolean existsByPlateNumber(String plateNumber);
}
