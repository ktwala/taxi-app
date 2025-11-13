package com.taxiservice.service.financial;

import com.taxiservice.dto.financial.BankPaymentRequest;
import com.taxiservice.dto.financial.BankPaymentResponse;
import com.taxiservice.entity.BankPayment;
import com.taxiservice.exception.DuplicateResourceException;
import com.taxiservice.exception.ResourceNotFoundException;
import com.taxiservice.repository.AssocMemberRepository;
import com.taxiservice.repository.BankPaymentRepository;
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
public class BankPaymentService {

    private final BankPaymentRepository bankPaymentRepository;
    private final AssocMemberRepository assocMemberRepository;

    public BankPaymentResponse recordBankPayment(BankPaymentRequest request) {
        log.info("Recording bank payment for member ID: {}", request.getAssocMemberId());

        // Verify member exists
        assocMemberRepository.findById(request.getAssocMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("AssocMember", "assocMemberId", request.getAssocMemberId()));

        // Check for duplicate transaction reference
        if (bankPaymentRepository.existsByTransactionReference(request.getTransactionReference())) {
            throw new DuplicateResourceException("BankPayment", "transactionReference", request.getTransactionReference());
        }

        BankPayment payment = BankPayment.builder()
                .assocMemberId(request.getAssocMemberId())
                .levyPaymentId(request.getLevyPaymentId())
                .levyFineId(request.getLevyFineId())
                .bankName(request.getBankName())
                .branchCode(request.getBranchCode())
                .accountNumber(request.getAccountNumber())
                .transactionReference(request.getTransactionReference())
                .amount(request.getAmount())
                .paymentDate(request.getPaymentDate())
                .verified(false)
                .build();

        BankPayment saved = bankPaymentRepository.save(payment);
        log.info("Bank payment recorded successfully with ID: {}", saved.getBankPaymentId());

        return convertToResponse(saved);
    }

    public BankPaymentResponse verifyPayment(Long bankPaymentId) {
        log.info("Verifying bank payment ID: {}", bankPaymentId);

        BankPayment payment = bankPaymentRepository.findById(bankPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("BankPayment", "bankPaymentId", bankPaymentId));

        if (payment.getVerified()) {
            throw new IllegalStateException("Payment has already been verified");
        }

        payment.setVerified(true);

        BankPayment updated = bankPaymentRepository.save(payment);
        log.info("Bank payment verified successfully");

        return convertToResponse(updated);
    }

    @Transactional(readOnly = true)
    public BankPaymentResponse getPaymentById(Long bankPaymentId) {
        log.info("Fetching bank payment with ID: {}", bankPaymentId);

        BankPayment payment = bankPaymentRepository.findById(bankPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("BankPayment", "bankPaymentId", bankPaymentId));

        return convertToResponse(payment);
    }

    @Transactional(readOnly = true)
    public BankPaymentResponse getPaymentByTransactionReference(String transactionReference) {
        log.info("Fetching bank payment by transaction reference: {}", transactionReference);

        BankPayment payment = bankPaymentRepository.findByTransactionReference(transactionReference)
                .orElseThrow(() -> new ResourceNotFoundException("BankPayment", "transactionReference", transactionReference));

        return convertToResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<BankPaymentResponse> getPaymentsByMember(Long memberId) {
        log.info("Fetching bank payments for member ID: {}", memberId);

        return bankPaymentRepository.findByAssocMemberId(memberId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BankPaymentResponse> getUnverifiedPayments() {
        log.info("Fetching unverified bank payments");

        return bankPaymentRepository.findUnverifiedPayments().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BankPaymentResponse> getVerifiedPayments() {
        log.info("Fetching verified bank payments");

        return bankPaymentRepository.findByVerified(true).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private BankPaymentResponse convertToResponse(BankPayment payment) {
        BankPaymentResponse response = BankPaymentResponse.builder()
                .bankPaymentId(payment.getBankPaymentId())
                .assocMemberId(payment.getAssocMemberId())
                .levyPaymentId(payment.getLevyPaymentId())
                .levyFineId(payment.getLevyFineId())
                .bankName(payment.getBankName())
                .branchCode(payment.getBranchCode())
                .accountNumber(payment.getAccountNumber())
                .transactionReference(payment.getTransactionReference())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .verified(payment.getVerified())
                .createdAt(payment.getCreatedAt())
                .build();

        // Fetch member name
        assocMemberRepository.findById(payment.getAssocMemberId())
                .ifPresent(member -> response.setMemberName(member.getName()));

        return response;
    }
}
