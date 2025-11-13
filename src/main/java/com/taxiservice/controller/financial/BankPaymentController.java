package com.taxiservice.controller.financial;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.financial.BankPaymentRequest;
import com.taxiservice.dto.financial.BankPaymentResponse;
import com.taxiservice.service.financial.BankPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bank-payments")
@RequiredArgsConstructor
@Slf4j
public class BankPaymentController {

    private final BankPaymentService bankPaymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<BankPaymentResponse>> recordBankPayment(@Valid @RequestBody BankPaymentRequest request) {
        log.info("REST request to record bank payment for member ID: {}", request.getAssocMemberId());
        BankPaymentResponse response = bankPaymentService.recordBankPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bank payment recorded successfully", response));
    }

    @PatchMapping("/{paymentId}/verify")
    public ResponseEntity<ApiResponse<BankPaymentResponse>> verifyPayment(
            @PathVariable Long paymentId,
            @RequestParam String verifiedBy) {
        log.info("REST request to verify bank payment ID: {}", paymentId);
        BankPaymentResponse response = bankPaymentService.verifyPayment(paymentId, verifiedBy);
        return ResponseEntity.ok(ApiResponse.success("Payment verified successfully", response));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<BankPaymentResponse>> getPaymentById(@PathVariable Long paymentId) {
        log.info("REST request to get bank payment with ID: {}", paymentId);
        BankPaymentResponse response = bankPaymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(ApiResponse.success("Payment retrieved successfully", response));
    }

    @GetMapping("/transaction/{transactionReference}")
    public ResponseEntity<ApiResponse<BankPaymentResponse>> getPaymentByTransactionReference(
            @PathVariable String transactionReference) {
        log.info("REST request to get bank payment by transaction reference: {}", transactionReference);
        BankPaymentResponse response = bankPaymentService.getPaymentByTransactionReference(transactionReference);
        return ResponseEntity.ok(ApiResponse.success("Payment retrieved successfully", response));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<BankPaymentResponse>>> getPaymentsByMember(@PathVariable Long memberId) {
        log.info("REST request to get bank payments for member ID: {}", memberId);
        List<BankPaymentResponse> responses = bankPaymentService.getPaymentsByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("Payments retrieved successfully", responses));
    }

    @GetMapping("/unverified")
    public ResponseEntity<ApiResponse<List<BankPaymentResponse>>> getUnverifiedPayments() {
        log.info("REST request to get unverified bank payments");
        List<BankPaymentResponse> responses = bankPaymentService.getUnverifiedPayments();
        return ResponseEntity.ok(ApiResponse.success("Unverified payments retrieved", responses));
    }

    @GetMapping("/verified")
    public ResponseEntity<ApiResponse<List<BankPaymentResponse>>> getVerifiedPayments() {
        log.info("REST request to get verified bank payments");
        List<BankPaymentResponse> responses = bankPaymentService.getVerifiedPayments();
        return ResponseEntity.ok(ApiResponse.success("Verified payments retrieved", responses));
    }
}
