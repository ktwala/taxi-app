package com.taxiservice.controller.financial;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.financial.LevyPaymentRequest;
import com.taxiservice.dto.financial.LevyPaymentResponse;
import com.taxiservice.service.financial.LevyPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/levy-payments")
@RequiredArgsConstructor
@Slf4j
public class LevyPaymentController {

    private final LevyPaymentService levyPaymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<LevyPaymentResponse>> recordLevyPayment(@Valid @RequestBody LevyPaymentRequest request) {
        log.info("REST request to record levy payment for member ID: {}", request.getAssocMemberId());
        LevyPaymentResponse response = levyPaymentService.recordLevyPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Levy payment recorded successfully", response));
    }

    @PatchMapping("/{paymentId}/process")
    public ResponseEntity<ApiResponse<LevyPaymentResponse>> processPayment(
            @PathVariable Long paymentId,
            @RequestParam Long paymentMethodId,
            @RequestParam String currentUser) {
        log.info("REST request to process levy payment ID: {}", paymentId);
        LevyPaymentResponse response = levyPaymentService.processPayment(paymentId, paymentMethodId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", response));
    }

    @PatchMapping("/{paymentId}/attach-receipt")
    public ResponseEntity<ApiResponse<LevyPaymentResponse>> attachReceipt(
            @PathVariable Long paymentId,
            @RequestParam Long receiptId) {
        log.info("REST request to attach receipt to levy payment ID: {}", paymentId);
        LevyPaymentResponse response = levyPaymentService.attachReceipt(paymentId, receiptId);
        return ResponseEntity.ok(ApiResponse.success("Receipt attached successfully", response));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<LevyPaymentResponse>> getPaymentById(@PathVariable Long paymentId) {
        log.info("REST request to get levy payment with ID: {}", paymentId);
        LevyPaymentResponse response = levyPaymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(ApiResponse.success("Payment retrieved successfully", response));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<LevyPaymentResponse>>> getPaymentsByMember(@PathVariable Long memberId) {
        log.info("REST request to get levy payments for member ID: {}", memberId);
        List<LevyPaymentResponse> responses = levyPaymentService.getPaymentsByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("Payments retrieved successfully", responses));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<LevyPaymentResponse>>> getPendingPayments() {
        log.info("REST request to get pending levy payments");
        List<LevyPaymentResponse> responses = levyPaymentService.getPendingPayments();
        return ResponseEntity.ok(ApiResponse.success("Pending payments retrieved", responses));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<LevyPaymentResponse>>> getPaymentsByStatus(@PathVariable String status) {
        log.info("REST request to get levy payments by status: {}", status);
        List<LevyPaymentResponse> responses = levyPaymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Payments retrieved successfully", responses));
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<LevyPaymentResponse>>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("REST request to get levy payments between {} and {}", startDate, endDate);
        List<LevyPaymentResponse> responses = levyPaymentService.getPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Payments retrieved successfully", responses));
    }

    @GetMapping("/member/{memberId}/total-paid")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalPaidByMember(@PathVariable Long memberId) {
        log.info("REST request to get total paid by member ID: {}", memberId);
        BigDecimal total = levyPaymentService.getTotalPaidByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("Total retrieved successfully", total));
    }

    @GetMapping("/total-collected")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalCollectedInPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("REST request to get total collected between {} and {}", startDate, endDate);
        BigDecimal total = levyPaymentService.getTotalCollectedInPeriod(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Total retrieved successfully", total));
    }

    @GetMapping("/member/{memberId}/count-pending")
    public ResponseEntity<ApiResponse<Long>> countPendingByMember(@PathVariable Long memberId) {
        log.info("REST request to count pending payments for member ID: {}", memberId);
        Long count = levyPaymentService.countPendingByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("Count retrieved successfully", count));
    }
}
