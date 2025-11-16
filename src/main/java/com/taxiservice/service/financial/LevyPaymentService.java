package com.taxiservice.service.financial;

import com.taxiservice.dto.financial.LevyPaymentRequest;
import com.taxiservice.dto.financial.LevyPaymentResponse;
import com.taxiservice.entity.LevyPayment;
import com.taxiservice.exception.ResourceNotFoundException;
import com.taxiservice.repository.AssocMemberRepository;
import com.taxiservice.repository.LevyPaymentRepository;
import com.taxiservice.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LevyPaymentService {

    private final LevyPaymentRepository levyPaymentRepository;
    private final AssocMemberRepository assocMemberRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public LevyPaymentResponse recordLevyPayment(LevyPaymentRequest request, String currentUser) {
        log.info("Recording levy payment for member ID: {} by user: {}", request.getAssocMemberId(), currentUser);

        // Verify member exists
        assocMemberRepository.findById(request.getAssocMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("AssocMember", "assocMemberId", request.getAssocMemberId()));

        // Verify payment method if provided
        if (request.getPaymentMethodId() != null) {
            paymentMethodRepository.findById(request.getPaymentMethodId())
                    .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "paymentMethodId", request.getPaymentMethodId()));
        }

        LevyPayment payment = LevyPayment.builder()
                .assocMemberId(request.getAssocMemberId())
                .weekStartDate(request.getWeekStartDate())
                .weekEndDate(request.getWeekEndDate())
                .amount(request.getAmount())
                .paymentStatus(request.getPaymentStatus())
                .paymentMethodId(request.getPaymentMethodId())
                .createdBy(currentUser)
                .build();

        LevyPayment saved = levyPaymentRepository.save(payment);
        log.info("Levy payment recorded successfully with ID: {}", saved.getLevyPaymentId());

        return convertToResponse(saved);
    }

    public LevyPaymentResponse processPayment(Long levyPaymentId, Long paymentMethodId, String currentUser) {
        log.info("Processing levy payment ID: {} with payment method ID: {}", levyPaymentId, paymentMethodId);

        LevyPayment payment = levyPaymentRepository.findById(levyPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("LevyPayment", "levyPaymentId", levyPaymentId));

        if ("Paid".equals(payment.getPaymentStatus())) {
            throw new IllegalStateException("Payment has already been processed");
        }

        // Verify payment method
        paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "paymentMethodId", paymentMethodId));

        payment.setPaymentStatus("Paid");
        payment.setPaymentMethodId(paymentMethodId);
        payment.setUpdatedBy(currentUser);

        LevyPayment updated = levyPaymentRepository.save(payment);
        log.info("Levy payment processed successfully");

        return convertToResponse(updated);
    }

    public LevyPaymentResponse attachReceipt(Long levyPaymentId, String receiptNumber) {
        log.info("Attaching receipt {} to levy payment ID: {}", receiptNumber, levyPaymentId);

        LevyPayment payment = levyPaymentRepository.findById(levyPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("LevyPayment", "levyPaymentId", levyPaymentId));

        payment.setReceiptNumber(receiptNumber);

        LevyPayment updated = levyPaymentRepository.save(payment);
        return convertToResponse(updated);
    }

    @Transactional(readOnly = true)
    public LevyPaymentResponse getPaymentById(Long paymentId) {
        log.info("Fetching levy payment with ID: {}", paymentId);

        LevyPayment payment = levyPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("LevyPayment", "levyPaymentId", paymentId));

        return convertToResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<LevyPaymentResponse> getPaymentsByMember(Long memberId) {
        log.info("Fetching levy payments for member ID: {}", memberId);

        return levyPaymentRepository.findByAssocMemberId(memberId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LevyPaymentResponse> getPendingPayments() {
        log.info("Fetching pending levy payments");

        return levyPaymentRepository.findPendingPayments().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LevyPaymentResponse> getPaymentsByStatus(String status) {
        log.info("Fetching levy payments with status: {}", status);

        return levyPaymentRepository.findByPaymentStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LevyPaymentResponse> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching levy payments between {} and {}", startDate, endDate);

        return levyPaymentRepository.findByDateRange(startDate, endDate).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPaidByMember(Long memberId) {
        log.info("Calculating total paid by member ID: {}", memberId);

        BigDecimal total = levyPaymentRepository.getTotalPaidByMember(memberId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalCollectedInPeriod(LocalDate startDate, LocalDate endDate) {
        log.info("Calculating total collected between {} and {}", startDate, endDate);

        BigDecimal total = levyPaymentRepository.getTotalCollectedInPeriod(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public Long countPendingByMember(Long memberId) {
        return levyPaymentRepository.countPendingByMember(memberId);
    }

    private LevyPaymentResponse convertToResponse(LevyPayment payment) {
        LevyPaymentResponse response = LevyPaymentResponse.builder()
                .levyPaymentId(payment.getLevyPaymentId())
                .assocMemberId(payment.getAssocMemberId())
                .weekStartDate(payment.getWeekStartDate())
                .weekEndDate(payment.getWeekEndDate())
                .amount(payment.getAmount())
                .paymentStatus(payment.getPaymentStatus())
                .paymentMethodId(payment.getPaymentMethodId())
                .receiptNumber(payment.getReceiptNumber())
                .createdBy(payment.getCreatedBy())
                .updatedBy(payment.getUpdatedBy())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();

        // Fetch member details
        assocMemberRepository.findById(payment.getAssocMemberId()).ifPresent(member -> {
            response.setMemberName(member.getName());
            response.setSquadNumber(member.getSquadNumber());
        });

        // Fetch payment method name
        if (payment.getPaymentMethodId() != null) {
            paymentMethodRepository.findById(payment.getPaymentMethodId())
                    .ifPresent(method -> response.setPaymentMethodName(method.getMethodName()));
        }

        return response;
    }
}
