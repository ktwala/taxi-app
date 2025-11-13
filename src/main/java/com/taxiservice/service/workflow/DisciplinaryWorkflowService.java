package com.taxiservice.service.workflow;

import com.taxiservice.dto.workflow.DisciplinaryWorkflowRequest;
import com.taxiservice.dto.workflow.DisciplinaryWorkflowResponse;
import com.taxiservice.dto.workflow.WorkflowDecisionRequest;
import com.taxiservice.entity.LevyFineDisciplinaryWorkflow;
import com.taxiservice.exception.ResourceNotFoundException;
import com.taxiservice.repository.AssocMemberRepository;
import com.taxiservice.repository.LevyFineDisciplinaryWorkflowRepository;
import com.taxiservice.repository.LevyFineRepository;
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
public class DisciplinaryWorkflowService {

    private final LevyFineDisciplinaryWorkflowRepository workflowRepository;
    private final LevyFineRepository levyFineRepository;
    private final AssocMemberRepository assocMemberRepository;

    public DisciplinaryWorkflowResponse initiateWorkflow(DisciplinaryWorkflowRequest request) {
        log.info("Initiating disciplinary workflow for fine ID: {}", request.getLevyFineId());

        // Verify fine exists
        levyFineRepository.findById(request.getLevyFineId())
                .orElseThrow(() -> new ResourceNotFoundException("LevyFine", "levyFineId", request.getLevyFineId()));

        // Verify member exists
        assocMemberRepository.findById(request.getAssocMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("AssocMember", "assocMemberId", request.getAssocMemberId()));

        // Check if workflow already exists for this fine
        if (workflowRepository.findByLevyFineId(request.getLevyFineId()).isPresent()) {
            throw new IllegalStateException("Disciplinary workflow already exists for this fine");
        }

        LevyFineDisciplinaryWorkflow workflow = LevyFineDisciplinaryWorkflow.builder()
                .levyFineId(request.getLevyFineId())
                .assocMemberId(request.getAssocMemberId())
                .caseStatement(request.getCaseStatement())
                .secretaryDecision("Pending")
                .chairpersonDecision("Pending")
                .finalStatus("Ongoing")
                .chairpersonOverride(false)
                .build();

        LevyFineDisciplinaryWorkflow saved = workflowRepository.save(workflow);
        log.info("Disciplinary workflow initiated successfully with ID: {}", saved.getWorkflowId());

        return convertToResponse(saved);
    }

    public DisciplinaryWorkflowResponse secretaryDecision(Long workflowId, WorkflowDecisionRequest request) {
        log.info("Secretary making decision on workflow ID: {} with decision: {}", workflowId, request.getDecision());

        LevyFineDisciplinaryWorkflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new ResourceNotFoundException("LevyFineDisciplinaryWorkflow", "workflowId", workflowId));

        if (!"Pending".equals(workflow.getSecretaryDecision())) {
            throw new IllegalStateException("Secretary has already made a decision on this workflow");
        }

        workflow.setSecretaryDecision(request.getDecision());
        if (request.getPaymentArrangement() != null) {
            workflow.setPaymentArrangement(request.getPaymentArrangement());
        }

        // If rejected by secretary, mark workflow as resolved
        if ("Rejected".equals(request.getDecision())) {
            workflow.setChairpersonDecision("Not Required");
            workflow.setFinalStatus("Resolved");
        }

        LevyFineDisciplinaryWorkflow updated = workflowRepository.save(workflow);
        log.info("Secretary decision recorded successfully");

        return convertToResponse(updated);
    }

    public DisciplinaryWorkflowResponse chairpersonDecision(Long workflowId, WorkflowDecisionRequest request) {
        log.info("Chairperson making decision on workflow ID: {} with decision: {}", workflowId, request.getDecision());

        LevyFineDisciplinaryWorkflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new ResourceNotFoundException("LevyFineDisciplinaryWorkflow", "workflowId", workflowId));

        if ("Pending".equals(workflow.getSecretaryDecision()) && !request.getOverride()) {
            throw new IllegalStateException("Secretary must make a decision first, unless overriding");
        }

        if (!"Pending".equals(workflow.getChairpersonDecision())) {
            throw new IllegalStateException("Chairperson has already made a decision on this workflow");
        }

        workflow.setChairpersonDecision(request.getDecision());
        workflow.setChairpersonOverride(request.getOverride());
        workflow.setFinalStatus("Resolved");

        if (request.getPaymentArrangement() != null) {
            String arrangement = workflow.getPaymentArrangement() != null ?
                    workflow.getPaymentArrangement() + "\nChairperson: " + request.getPaymentArrangement() :
                    "Chairperson: " + request.getPaymentArrangement();
            workflow.setPaymentArrangement(arrangement);
        }

        LevyFineDisciplinaryWorkflow updated = workflowRepository.save(workflow);
        log.info("Chairperson decision recorded successfully");

        return convertToResponse(updated);
    }

    @Transactional(readOnly = true)
    public DisciplinaryWorkflowResponse getWorkflowById(Long workflowId) {
        log.info("Fetching workflow with ID: {}", workflowId);

        LevyFineDisciplinaryWorkflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new ResourceNotFoundException("LevyFineDisciplinaryWorkflow", "workflowId", workflowId));

        return convertToResponse(workflow);
    }

    @Transactional(readOnly = true)
    public DisciplinaryWorkflowResponse getWorkflowByFineId(Long fineId) {
        log.info("Fetching workflow for fine ID: {}", fineId);

        LevyFineDisciplinaryWorkflow workflow = workflowRepository.findByLevyFineId(fineId)
                .orElseThrow(() -> new ResourceNotFoundException("LevyFineDisciplinaryWorkflow", "levyFineId", fineId));

        return convertToResponse(workflow);
    }

    @Transactional(readOnly = true)
    public List<DisciplinaryWorkflowResponse> getWorkflowsByMember(Long memberId) {
        log.info("Fetching workflows for member ID: {}", memberId);

        return workflowRepository.findByAssocMemberId(memberId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DisciplinaryWorkflowResponse> getPendingSecretaryDecisions() {
        log.info("Fetching workflows pending secretary decision");

        return workflowRepository.findPendingSecretaryDecision().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DisciplinaryWorkflowResponse> getPendingChairpersonDecisions() {
        log.info("Fetching workflows pending chairperson decision");

        return workflowRepository.findPendingChairpersonDecision().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DisciplinaryWorkflowResponse> getOngoingWorkflows() {
        log.info("Fetching ongoing workflows");

        return workflowRepository.findOngoingWorkflows().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private DisciplinaryWorkflowResponse convertToResponse(LevyFineDisciplinaryWorkflow workflow) {
        DisciplinaryWorkflowResponse response = DisciplinaryWorkflowResponse.builder()
                .workflowId(workflow.getWorkflowId())
                .levyFineId(workflow.getLevyFineId())
                .assocMemberId(workflow.getAssocMemberId())
                .caseStatement(workflow.getCaseStatement())
                .secretaryDecision(workflow.getSecretaryDecision())
                .chairpersonDecision(workflow.getChairpersonDecision())
                .paymentArrangement(workflow.getPaymentArrangement())
                .chairpersonOverride(workflow.getChairpersonOverride())
                .finalStatus(workflow.getFinalStatus())
                .createdAt(workflow.getCreatedAt())
                .updatedAt(workflow.getUpdatedAt())
                .build();

        // Fetch member name
        assocMemberRepository.findById(workflow.getAssocMemberId())
                .ifPresent(member -> response.setMemberName(member.getName()));

        return response;
    }
}
