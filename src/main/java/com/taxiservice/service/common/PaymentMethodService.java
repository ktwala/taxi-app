package com.taxiservice.service.common;

import com.taxiservice.dto.common.PaymentMethodRequest;
import com.taxiservice.dto.common.PaymentMethodResponse;
import com.taxiservice.entity.PaymentMethod;
import com.taxiservice.exception.DuplicateResourceException;
import com.taxiservice.exception.ResourceNotFoundException;
import com.taxiservice.repository.PaymentMethodRepository;
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
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodResponse createPaymentMethod(PaymentMethodRequest request) {
        log.info("Creating payment method: {}", request.getMethodName());

        if (paymentMethodRepository.existsByMethodName(request.getMethodName())) {
            throw new DuplicateResourceException("PaymentMethod", "methodName", request.getMethodName());
        }

        PaymentMethod paymentMethod = PaymentMethod.builder()
                .methodName(request.getMethodName())
                .description(request.getDescription())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        PaymentMethod saved = paymentMethodRepository.save(paymentMethod);
        log.info("Payment method created successfully with ID: {}", saved.getPaymentMethodId());

        return convertToResponse(saved);
    }

    public PaymentMethodResponse updatePaymentMethod(Long paymentMethodId, PaymentMethodRequest request) {
        log.info("Updating payment method with ID: {}", paymentMethodId);

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "paymentMethodId", paymentMethodId));

        if (!paymentMethod.getMethodName().equals(request.getMethodName()) &&
                paymentMethodRepository.existsByMethodName(request.getMethodName())) {
            throw new DuplicateResourceException("PaymentMethod", "methodName", request.getMethodName());
        }

        paymentMethod.setMethodName(request.getMethodName());
        paymentMethod.setDescription(request.getDescription());
        if (request.getActive() != null) {
            paymentMethod.setActive(request.getActive());
        }

        PaymentMethod updated = paymentMethodRepository.save(paymentMethod);
        log.info("Payment method updated successfully");

        return convertToResponse(updated);
    }

    public void deletePaymentMethod(Long paymentMethodId) {
        log.info("Deleting payment method with ID: {}", paymentMethodId);

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "paymentMethodId", paymentMethodId));

        paymentMethodRepository.delete(paymentMethod);
        log.info("Payment method deleted successfully");
    }

    public PaymentMethodResponse activatePaymentMethod(Long paymentMethodId) {
        log.info("Activating payment method with ID: {}", paymentMethodId);

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "paymentMethodId", paymentMethodId));

        paymentMethod.setActive(true);
        PaymentMethod updated = paymentMethodRepository.save(paymentMethod);

        return convertToResponse(updated);
    }

    public PaymentMethodResponse deactivatePaymentMethod(Long paymentMethodId) {
        log.info("Deactivating payment method with ID: {}", paymentMethodId);

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "paymentMethodId", paymentMethodId));

        paymentMethod.setActive(false);
        PaymentMethod updated = paymentMethodRepository.save(paymentMethod);

        return convertToResponse(updated);
    }

    @Transactional(readOnly = true)
    public PaymentMethodResponse getPaymentMethodById(Long paymentMethodId) {
        log.info("Fetching payment method with ID: {}", paymentMethodId);

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "paymentMethodId", paymentMethodId));

        return convertToResponse(paymentMethod);
    }

    @Transactional(readOnly = true)
    public PaymentMethodResponse getPaymentMethodByName(String methodName) {
        log.info("Fetching payment method by name: {}", methodName);

        PaymentMethod paymentMethod = paymentMethodRepository.findByMethodName(methodName)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "methodName", methodName));

        return convertToResponse(paymentMethod);
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> getAllPaymentMethods() {
        log.info("Fetching all payment methods");

        return paymentMethodRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> getActivePaymentMethods() {
        log.info("Fetching active payment methods");

        return paymentMethodRepository.findByActive(true).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private PaymentMethodResponse convertToResponse(PaymentMethod paymentMethod) {
        return PaymentMethodResponse.builder()
                .paymentMethodId(paymentMethod.getPaymentMethodId())
                .methodName(paymentMethod.getMethodName())
                .description(paymentMethod.getDescription())
                .active(paymentMethod.getActive())
                .createdAt(paymentMethod.getCreatedAt())
                .updatedAt(paymentMethod.getUpdatedAt())
                .build();
    }
}
