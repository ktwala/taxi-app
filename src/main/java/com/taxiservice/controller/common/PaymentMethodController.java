package com.taxiservice.controller.common;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.common.PaymentMethodRequest;
import com.taxiservice.dto.common.PaymentMethodResponse;
import com.taxiservice.service.common.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
@Slf4j
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> createPaymentMethod(@Valid @RequestBody PaymentMethodRequest request) {
        log.info("REST request to create payment method: {}", request.getMethodName());
        PaymentMethodResponse response = paymentMethodService.createPaymentMethod(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment method created successfully", response));
    }

    @PutMapping("/{paymentMethodId}")
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> updatePaymentMethod(
            @PathVariable Long paymentMethodId,
            @Valid @RequestBody PaymentMethodRequest request) {
        log.info("REST request to update payment method with ID: {}", paymentMethodId);
        PaymentMethodResponse response = paymentMethodService.updatePaymentMethod(paymentMethodId, request);
        return ResponseEntity.ok(ApiResponse.success("Payment method updated successfully", response));
    }

    @DeleteMapping("/{paymentMethodId}")
    public ResponseEntity<ApiResponse<Void>> deletePaymentMethod(@PathVariable Long paymentMethodId) {
        log.info("REST request to delete payment method with ID: {}", paymentMethodId);
        paymentMethodService.deletePaymentMethod(paymentMethodId);
        return ResponseEntity.ok(ApiResponse.success("Payment method deleted successfully", null));
    }

    @PatchMapping("/{paymentMethodId}/activate")
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> activatePaymentMethod(@PathVariable Long paymentMethodId) {
        log.info("REST request to activate payment method with ID: {}", paymentMethodId);
        PaymentMethodResponse response = paymentMethodService.activatePaymentMethod(paymentMethodId);
        return ResponseEntity.ok(ApiResponse.success("Payment method activated successfully", response));
    }

    @PatchMapping("/{paymentMethodId}/deactivate")
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> deactivatePaymentMethod(@PathVariable Long paymentMethodId) {
        log.info("REST request to deactivate payment method with ID: {}", paymentMethodId);
        PaymentMethodResponse response = paymentMethodService.deactivatePaymentMethod(paymentMethodId);
        return ResponseEntity.ok(ApiResponse.success("Payment method deactivated successfully", response));
    }

    @GetMapping("/{paymentMethodId}")
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> getPaymentMethodById(@PathVariable Long paymentMethodId) {
        log.info("REST request to get payment method with ID: {}", paymentMethodId);
        PaymentMethodResponse response = paymentMethodService.getPaymentMethodById(paymentMethodId);
        return ResponseEntity.ok(ApiResponse.success("Payment method retrieved successfully", response));
    }

    @GetMapping("/name/{methodName}")
    public ResponseEntity<ApiResponse<PaymentMethodResponse>> getPaymentMethodByName(@PathVariable String methodName) {
        log.info("REST request to get payment method by name: {}", methodName);
        PaymentMethodResponse response = paymentMethodService.getPaymentMethodByName(methodName);
        return ResponseEntity.ok(ApiResponse.success("Payment method retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentMethodResponse>>> getAllPaymentMethods() {
        log.info("REST request to get all payment methods");
        List<PaymentMethodResponse> responses = paymentMethodService.getAllPaymentMethods();
        return ResponseEntity.ok(ApiResponse.success("Payment methods retrieved successfully", responses));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PaymentMethodResponse>>> getActivePaymentMethods() {
        log.info("REST request to get active payment methods");
        List<PaymentMethodResponse> responses = paymentMethodService.getActivePaymentMethods();
        return ResponseEntity.ok(ApiResponse.success("Active payment methods retrieved", responses));
    }
}
