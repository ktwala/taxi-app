package com.taxiservice.repository;

import com.taxiservice.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByIsActive(Boolean isActive);

    @Query("SELECT r FROM Route r WHERE r.isActive = true")
    List<Route> findAllActiveRoutes();

    List<Route> findByNameContainingIgnoreCase(String name);
}
