package com.taxiservice.repository;

import com.taxiservice.entity.MembershipApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipApplicationRepository extends JpaRepository<MembershipApplication, Long> {

    List<MembershipApplication> findByApplicationStatus(String status);

    List<MembershipApplication> findByRouteId(Long routeId);

    List<MembershipApplication> findBySecretaryReviewed(Boolean reviewed);

    List<MembershipApplication> findByChairpersonReviewed(Boolean reviewed);

    @Query("SELECT m FROM MembershipApplication m WHERE m.applicationStatus = 'Pending' AND m.secretaryReviewed = false")
    List<MembershipApplication> findPendingSecretaryReview();

    @Query("SELECT m FROM MembershipApplication m WHERE m.applicationStatus = 'Pending' AND m.secretaryReviewed = true AND m.chairpersonReviewed = false")
    List<MembershipApplication> findPendingChairpersonReview();

    @Query("SELECT COUNT(m) FROM MembershipApplication m WHERE m.applicationStatus = ?1")
    Long countByStatus(String status);
}
