package com.taxiservice.controller.financial;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.financial.LevyFineRequest;
import com.taxiservice.dto.financial.LevyFineResponse;
import com.taxiservice.service.financial.LevyFineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/levy-fines")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'CHAIRPERSON')")
public class LevyFineController {

    private final LevyFineService levyFineService;

    @PostMapping
    public ResponseEntity<ApiResponse<LevyFineResponse>> issueFine(
            @Valid @RequestBody LevyFineRequest request,
            @RequestParam String currentUser) {
        log.info("REST request to issue fine for member ID: {} by user: {}", request.getAssocMemberId(), currentUser);
        LevyFineResponse response = levyFineService.issueFine(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Fine issued successfully", response));
    }

    @PatchMapping("/{fineId}/process-payment")
    public ResponseEntity<ApiResponse<LevyFineResponse>> processFinePayment(
            @PathVariable Long fineId,
            @RequestParam Long paymentMethodId,
            @RequestParam String currentUser) {
        log.info("REST request to process payment for fine ID: {} by user: {}", fineId, currentUser);
        LevyFineResponse response = levyFineService.processFinePayment(fineId, paymentMethodId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Fine payment processed", response));
    }

    @PatchMapping("/{fineId}/status")
    public ResponseEntity<ApiResponse<LevyFineResponse>> updateFineStatus(
            @PathVariable Long fineId,
            @RequestParam String status,
            @RequestParam String currentUser) {
        log.info("REST request to update fine ID {} to status: {}", fineId, status);
        LevyFineResponse response = levyFineService.updateFineStatus(fineId, status, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Fine status updated", response));
    }

    @PatchMapping("/{fineId}/attach-receipt")
    public ResponseEntity<ApiResponse<LevyFineResponse>> attachReceipt(
            @PathVariable Long fineId,
            @RequestParam String receiptNumber) {
        log.info("REST request to attach receipt {} to fine ID: {}", receiptNumber, fineId);
        LevyFineResponse response = levyFineService.attachReceipt(fineId, receiptNumber);
        return ResponseEntity.ok(ApiResponse.success("Receipt attached successfully", response));
    }

    @GetMapping("/{fineId}")
    public ResponseEntity<ApiResponse<LevyFineResponse>> getFineById(@PathVariable Long fineId) {
        log.info("REST request to get fine with ID: {}", fineId);
        LevyFineResponse response = levyFineService.getFineById(fineId);
        return ResponseEntity.ok(ApiResponse.success("Fine retrieved successfully", response));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<LevyFineResponse>>> getFinesByMember(@PathVariable Long memberId) {
        log.info("REST request to get fines for member ID: {}", memberId);
        List<LevyFineResponse> responses = levyFineService.getFinesByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("Fines retrieved successfully", responses));
    }

    @GetMapping("/unpaid")
    public ResponseEntity<ApiResponse<List<LevyFineResponse>>> getUnpaidFines() {
        log.info("REST request to get unpaid fines");
        List<LevyFineResponse> responses = levyFineService.getUnpaidFines();
        return ResponseEntity.ok(ApiResponse.success("Unpaid fines retrieved", responses));
    }

    @GetMapping("/owing")
    public ResponseEntity<ApiResponse<List<LevyFineResponse>>> getOwingFines() {
        log.info("REST request to get owing fines");
        List<LevyFineResponse> responses = levyFineService.getOwingFines();
        return ResponseEntity.ok(ApiResponse.success("Owing fines retrieved", responses));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<LevyFineResponse>>> getFinesByStatus(@PathVariable String status) {
        log.info("REST request to get fines by status: {}", status);
        List<LevyFineResponse> responses = levyFineService.getFinesByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Fines retrieved successfully", responses));
    }

    @GetMapping("/member/{memberId}/total-outstanding")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalOutstandingByMember(@PathVariable Long memberId) {
        log.info("REST request to get total outstanding fines for member ID: {}", memberId);
        BigDecimal total = levyFineService.getTotalOutstandingByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("Total outstanding retrieved", total));
    }

    @GetMapping("/total-collected")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalFinesCollected() {
        log.info("REST request to get total fines collected");
        BigDecimal total = levyFineService.getTotalFinesCollected();
        return ResponseEntity.ok(ApiResponse.success("Total collected retrieved", total));
    }

    @GetMapping("/member/{memberId}/count-outstanding")
    public ResponseEntity<ApiResponse<Long>> countOutstandingByMember(@PathVariable Long memberId) {
        log.info("REST request to count outstanding fines for member ID: {}", memberId);
        Long count = levyFineService.countOutstandingByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("Count retrieved successfully", count));
    }
}
