package com.taxiservice.repository;

import com.taxiservice.entity.MemberFinance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberFinanceRepository extends JpaRepository<MemberFinance, Long> {

    Optional<MemberFinance> findByAssocMemberId(Long assocMemberId);

    List<MemberFinance> findByJoiningFeePaid(Boolean paid);

    List<MemberFinance> findByMembershipCardIssued(Boolean issued);

    @Query("SELECT m FROM MemberFinance m WHERE m.joiningFeePaid = false")
    List<MemberFinance> findPendingJoiningFees();

    @Query("SELECT m FROM MemberFinance m WHERE m.joiningFeePaid = true AND m.membershipCardIssued = false")
    List<MemberFinance> findPendingMembershipCards();
}
