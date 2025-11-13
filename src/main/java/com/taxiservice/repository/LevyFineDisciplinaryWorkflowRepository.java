package com.taxiservice.repository;

import com.taxiservice.entity.LevyFineDisciplinaryWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LevyFineDisciplinaryWorkflowRepository extends JpaRepository<LevyFineDisciplinaryWorkflow, Long> {

    Optional<LevyFineDisciplinaryWorkflow> findByLevyFineId(Long levyFineId);

    List<LevyFineDisciplinaryWorkflow> findByAssocMemberId(Long assocMemberId);

    List<LevyFineDisciplinaryWorkflow> findByFinalStatus(String status);

    List<LevyFineDisciplinaryWorkflow> findBySecretaryDecision(String decision);

    List<LevyFineDisciplinaryWorkflow> findByChairpersonDecision(String decision);

    @Query("SELECT w FROM LevyFineDisciplinaryWorkflow w WHERE w.secretaryDecision = 'Pending'")
    List<LevyFineDisciplinaryWorkflow> findPendingSecretaryDecision();

    @Query("SELECT w FROM LevyFineDisciplinaryWorkflow w WHERE w.chairpersonDecision = 'Pending' AND w.secretaryDecision != 'Pending'")
    List<LevyFineDisciplinaryWorkflow> findPendingChairpersonDecision();

    @Query("SELECT w FROM LevyFineDisciplinaryWorkflow w WHERE w.finalStatus = 'Ongoing'")
    List<LevyFineDisciplinaryWorkflow> findOngoingWorkflows();
}
