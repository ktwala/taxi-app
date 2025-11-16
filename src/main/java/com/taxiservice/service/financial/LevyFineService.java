package com.taxiservice.service.financial;

import com.taxiservice.dto.financial.LevyFineRequest;
import com.taxiservice.dto.financial.LevyFineResponse;
import com.taxiservice.entity.LevyFine;
import com.taxiservice.exception.ResourceNotFoundException;
import com.taxiservice.repository.AssocMemberRepository;
import com.taxiservice.repository.LevyFineRepository;
import com.taxiservice.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LevyFineService {

    private final LevyFineRepository levyFineRepository;
    private final AssocMemberRepository assocMemberRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public LevyFineResponse issueFine(LevyFineRequest request, String currentUser) {
        log.info("Issuing fine to member ID: {} by user: {}", request.getAssocMemberId(), currentUser);

        // Verify member exists
        assocMemberRepository.findById(request.getAssocMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("AssocMember", "assocMemberId", request.getAssocMemberId()));

        // Verify payment method if provided
        if (request.getPaymentMethodId() != null) {
            paymentMethodRepository.findById(request.getPaymentMethodId())
                    .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "paymentMethodId", request.getPaymentMethodId()));
        }

        LevyFine fine = LevyFine.builder()
                .assocMemberId(request.getAssocMemberId())
                .fineAmount(request.getFineAmount())
                .fineReason(request.getFineReason())
                .fineStatus(request.getFineStatus())
                .paymentMethodId(request.getPaymentMethodId())
                .createdBy(currentUser)
                .build();

        LevyFine saved = levyFineRepository.save(fine);
        log.info("Fine issued successfully with ID: {}", saved.getLevyFineId());

        return convertToResponse(saved);
    }

    public LevyFineResponse processFinePayment(Long fineId, Long paymentMethodId, String currentUser) {
        log.info("Processing fine payment for fine ID: {} with payment method ID: {}", fineId, paymentMethodId);

        LevyFine fine = levyFineRepository.findById(fineId)
                .orElseThrow(() -> new ResourceNotFoundException("LevyFine", "levyFineId", fineId));

        if ("Paid".equals(fine.getFineStatus())) {
            throw new IllegalStateException("Fine has already been paid");
        }

        // Verify payment method
        paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "paymentMethodId", paymentMethodId));

        fine.setFineStatus("Paid");
        fine.setPaymentMethodId(paymentMethodId);
        fine.setUpdatedBy(currentUser);

        LevyFine updated = levyFineRepository.save(fine);
        log.info("Fine payment processed successfully");

        return convertToResponse(updated);
    }

    public LevyFineResponse updateFineStatus(Long fineId, String status, String currentUser) {
        log.info("Updating fine ID: {} to status: {}", fineId, status);

        LevyFine fine = levyFineRepository.findById(fineId)
                .orElseThrow(() -> new ResourceNotFoundException("LevyFine", "levyFineId", fineId));

        fine.setFineStatus(status);
        fine.setUpdatedBy(currentUser);

        LevyFine updated = levyFineRepository.save(fine);
        return convertToResponse(updated);
    }

    public LevyFineResponse attachReceipt(Long fineId, String receiptNumber) {
        log.info("Attaching receipt {} to fine ID: {}", receiptNumber, fineId);

        LevyFine fine = levyFineRepository.findById(fineId)
                .orElseThrow(() -> new ResourceNotFoundException("LevyFine", "levyFineId", fineId));

        fine.setReceiptNumber(receiptNumber);

        LevyFine updated = levyFineRepository.save(fine);
        return convertToResponse(updated);
    }

    @Transactional(readOnly = true)
    public LevyFineResponse getFineById(Long fineId) {
        log.info("Fetching fine with ID: {}", fineId);

        LevyFine fine = levyFineRepository.findById(fineId)
                .orElseThrow(() -> new ResourceNotFoundException("LevyFine", "levyFineId", fineId));

        return convertToResponse(fine);
    }

    @Transactional(readOnly = true)
    public List<LevyFineResponse> getFinesByMember(Long memberId) {
        log.info("Fetching fines for member ID: {}", memberId);

        return levyFineRepository.findByAssocMemberId(memberId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LevyFineResponse> getUnpaidFines() {
        log.info("Fetching unpaid fines");

        return levyFineRepository.findUnpaidFines().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LevyFineResponse> getOwingFines() {
        log.info("Fetching owing fines");

        return levyFineRepository.findOwingFines().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LevyFineResponse> getFinesByStatus(String status) {
        log.info("Fetching fines with status: {}", status);

        return levyFineRepository.findByFineStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalOutstandingByMember(Long memberId) {
        log.info("Calculating total outstanding fines for member ID: {}", memberId);

        BigDecimal total = levyFineRepository.getTotalOutstandingByMember(memberId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalFinesCollected() {
        log.info("Calculating total fines collected");

        BigDecimal total = levyFineRepository.getTotalFinesCollected();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public Long countOutstandingByMember(Long memberId) {
        return levyFineRepository.countOutstandingByMember(memberId);
    }

    private LevyFineResponse convertToResponse(LevyFine fine) {
        LevyFineResponse response = LevyFineResponse.builder()
                .levyFineId(fine.getLevyFineId())
                .assocMemberId(fine.getAssocMemberId())
                .fineAmount(fine.getFineAmount())
                .fineReason(fine.getFineReason())
                .fineStatus(fine.getFineStatus())
                .paymentMethodId(fine.getPaymentMethodId())
                .receiptNumber(fine.getReceiptNumber())
                .createdBy(fine.getCreatedBy())
                .updatedBy(fine.getUpdatedBy())
                .createdAt(fine.getCreatedAt())
                .updatedAt(fine.getUpdatedAt())
                .build();

        // Fetch member details
        assocMemberRepository.findById(fine.getAssocMemberId()).ifPresent(member -> {
            response.setMemberName(member.getName());
            response.setSquadNumber(member.getSquadNumber());
        });

        // Fetch payment method name
        if (fine.getPaymentMethodId() != null) {
            paymentMethodRepository.findById(fine.getPaymentMethodId())
                    .ifPresent(method -> response.setPaymentMethodName(method.getMethodName()));
        }

        return response;
    }
}
