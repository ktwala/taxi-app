package com.taxiservice.controller.workflow;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.workflow.DisciplinaryWorkflowResponse;
import com.taxiservice.dto.workflow.WorkflowDecisionRequest;
import com.taxiservice.service.workflow.DisciplinaryWorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disciplinary-workflows")
@RequiredArgsConstructor
@Slf4j
public class DisciplinaryWorkflowController {

    private final DisciplinaryWorkflowService workflowService;

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<DisciplinaryWorkflowResponse>> initiateWorkflow(@Valid @RequestBody DisciplinaryWorkflowRequest request) {
        log.info("REST request to initiate disciplinary workflow for fine ID: {}", request.getLevyFineId());
        DisciplinaryWorkflowResponse response = workflowService.initiateWorkflow(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Workflow initiated successfully", response));
    }

    @PatchMapping("/{workflowId}/secretary-decision")
    public ResponseEntity<ApiResponse<DisciplinaryWorkflowResponse>> secretaryDecision(
            @PathVariable Long workflowId,
            @Valid @RequestBody WorkflowDecisionRequest request) {
        log.info("REST request for secretary decision on workflow ID: {}", workflowId);
        DisciplinaryWorkflowResponse response = workflowService.secretaryDecision(workflowId, request);
        return ResponseEntity.ok(ApiResponse.success("Secretary decision recorded", response));
    }

    @PatchMapping("/{workflowId}/chairperson-decision")
    public ResponseEntity<ApiResponse<DisciplinaryWorkflowResponse>> chairpersonDecision(
            @PathVariable Long workflowId,
            @Valid @RequestBody WorkflowDecisionRequest request) {
        log.info("REST request for chairperson decision on workflow ID: {}", workflowId);
        DisciplinaryWorkflowResponse response = workflowService.chairpersonDecision(workflowId, request);
        return ResponseEntity.ok(ApiResponse.success("Chairperson decision recorded", response));
    }

    @GetMapping("/{workflowId}")
    public ResponseEntity<ApiResponse<DisciplinaryWorkflowResponse>> getWorkflowById(@PathVariable Long workflowId) {
        log.info("REST request to get workflow with ID: {}", workflowId);
        DisciplinaryWorkflowResponse response = workflowService.getWorkflowById(workflowId);
        return ResponseEntity.ok(ApiResponse.success("Workflow retrieved successfully", response));
    }

    @GetMapping("/fine/{fineId}")
    public ResponseEntity<ApiResponse<DisciplinaryWorkflowResponse>> getWorkflowByFineId(@PathVariable Long fineId) {
        log.info("REST request to get workflow for fine ID: {}", fineId);
        DisciplinaryWorkflowResponse response = workflowService.getWorkflowByFineId(fineId);
        return ResponseEntity.ok(ApiResponse.success("Workflow retrieved successfully", response));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<DisciplinaryWorkflowResponse>>> getWorkflowsByMember(@PathVariable Long memberId) {
        log.info("REST request to get workflows for member ID: {}", memberId);
        List<DisciplinaryWorkflowResponse> responses = workflowService.getWorkflowsByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("Workflows retrieved successfully", responses));
    }

    @GetMapping("/pending/secretary")
    public ResponseEntity<ApiResponse<List<DisciplinaryWorkflowResponse>>> getPendingSecretaryDecisions() {
        log.info("REST request to get workflows pending secretary decision");
        List<DisciplinaryWorkflowResponse> responses = workflowService.getPendingSecretaryDecisions();
        return ResponseEntity.ok(ApiResponse.success("Pending workflows retrieved", responses));
    }

    @GetMapping("/pending/chairperson")
    public ResponseEntity<ApiResponse<List<DisciplinaryWorkflowResponse>>> getPendingChairpersonDecisions() {
        log.info("REST request to get workflows pending chairperson decision");
        List<DisciplinaryWorkflowResponse> responses = workflowService.getPendingChairpersonDecisions();
        return ResponseEntity.ok(ApiResponse.success("Pending workflows retrieved", responses));
    }

    @GetMapping("/ongoing")
    public ResponseEntity<ApiResponse<List<DisciplinaryWorkflowResponse>>> getOngoingWorkflows() {
        log.info("REST request to get ongoing workflows");
        List<DisciplinaryWorkflowResponse> responses = workflowService.getOngoingWorkflows();
        return ResponseEntity.ok(ApiResponse.success("Ongoing workflows retrieved", responses));
    }
}
