package com.taxiservice.repository;

import com.taxiservice.entity.AssocMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssocMemberRepository extends JpaRepository<AssocMember, Long> {

    Optional<AssocMember> findBySquadNumber(String squadNumber);

    List<AssocMember> findByBlacklisted(Boolean blacklisted);

    List<AssocMember> findByNameContainingIgnoreCase(String name);

    boolean existsBySquadNumber(String squadNumber);

    @Query("SELECT a FROM AssocMember a WHERE a.blacklisted = false")
    List<AssocMember> findAllActiveMembers();

    @Query("SELECT COUNT(a) FROM AssocMember a WHERE a.blacklisted = false")
    Long countActiveMembers();
}
