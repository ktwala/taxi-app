package com.taxiservice.repository;

import com.taxiservice.entity.LevyFine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LevyFineRepository extends JpaRepository<LevyFine, Long> {

    List<LevyFine> findByAssocMemberId(Long assocMemberId);

    List<LevyFine> findByFineStatus(String status);

    List<LevyFine> findByAssocMemberIdAndFineStatus(Long assocMemberId, String status);

    @Query("SELECT l FROM LevyFine l WHERE l.fineStatus = 'Unpaid'")
    List<LevyFine> findUnpaidFines();

    @Query("SELECT l FROM LevyFine l WHERE l.fineStatus = 'Owing'")
    List<LevyFine> findOwingFines();

    @Query("SELECT SUM(l.fineAmount) FROM LevyFine l WHERE l.assocMemberId = :memberId AND l.fineStatus IN ('Unpaid', 'Owing')")
    BigDecimal getTotalOutstandingByMember(@Param("memberId") Long memberId);

    @Query("SELECT SUM(l.fineAmount) FROM LevyFine l WHERE l.fineStatus = 'Paid'")
    BigDecimal getTotalFinesCollected();

    @Query("SELECT COUNT(l) FROM LevyFine l WHERE l.assocMemberId = :memberId AND l.fineStatus IN ('Unpaid', 'Owing')")
    Long countOutstandingByMember(@Param("memberId") Long memberId);
}
