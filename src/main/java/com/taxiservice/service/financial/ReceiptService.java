package com.taxiservice.service.financial;

import com.taxiservice.dto.financial.ReceiptRequest;
import com.taxiservice.dto.financial.ReceiptResponse;
import com.taxiservice.entity.Receipt;
import com.taxiservice.exception.DuplicateResourceException;
import com.taxiservice.exception.ResourceNotFoundException;
import com.taxiservice.repository.AssocMemberRepository;
import com.taxiservice.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final AssocMemberRepository assocMemberRepository;

    public ReceiptResponse generateReceipt(ReceiptRequest request) {
        log.info("Generating receipt for member ID: {}", request.getAssocMemberId());

        // Verify member exists
        assocMemberRepository.findById(request.getAssocMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("AssocMember", "assocMemberId", request.getAssocMemberId()));

        // Check for duplicate receipt number
        if (receiptRepository.existsByReceiptNumber(request.getReceiptNumber())) {
            throw new DuplicateResourceException("Receipt", "receiptNumber", request.getReceiptNumber());
        }

        Receipt receipt = Receipt.builder()
                .assocMemberId(request.getAssocMemberId())
                .levyPaymentId(request.getLevyPaymentId())
                .levyFineId(request.getLevyFineId())
                .bankPaymentId(request.getBankPaymentId())
                .receiptNumber(request.getReceiptNumber())
                .issuedBy(request.getIssuedBy())
                .build();

        Receipt saved = receiptRepository.save(receipt);
        log.info("Receipt generated successfully with ID: {}", saved.getReceiptId());

        return convertToResponse(saved);
    }

    @Transactional(readOnly = true)
    public ReceiptResponse getReceiptById(Long receiptId) {
        log.info("Fetching receipt with ID: {}", receiptId);

        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt", "receiptId", receiptId));

        return convertToResponse(receipt);
    }

    @Transactional(readOnly = true)
    public ReceiptResponse getReceiptByNumber(String receiptNumber) {
        log.info("Fetching receipt by number: {}", receiptNumber);

        Receipt receipt = receiptRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt", "receiptNumber", receiptNumber));

        return convertToResponse(receipt);
    }

    @Transactional(readOnly = true)
    public List<ReceiptResponse> getReceiptsByMember(Long memberId) {
        log.info("Fetching receipts for member ID: {}", memberId);

        return receiptRepository.findByAssocMemberId(memberId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReceiptResponse> getReceiptsByIssuedBy(String issuedBy) {
        log.info("Fetching receipts issued by: {}", issuedBy);

        return receiptRepository.findByIssuedBy(issuedBy).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReceiptResponse> getReceiptsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching receipts between {} and {}", startDate, endDate);

        return receiptRepository.findByDateRange(startDate, endDate).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReceiptResponse> getAllReceipts() {
        log.info("Fetching all receipts");

        return receiptRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private ReceiptResponse convertToResponse(Receipt receipt) {
        ReceiptResponse response = ReceiptResponse.builder()
                .receiptId(receipt.getReceiptId())
                .assocMemberId(receipt.getAssocMemberId())
                .levyPaymentId(receipt.getLevyPaymentId())
                .levyFineId(receipt.getLevyFineId())
                .bankPaymentId(receipt.getBankPaymentId())
                .receiptNumber(receipt.getReceiptNumber())
                .issuedBy(receipt.getIssuedBy())
                .issuedDate(receipt.getIssuedDate())
                .build();

        // Fetch member name
        assocMemberRepository.findById(receipt.getAssocMemberId())
                .ifPresent(member -> response.setMemberName(member.getName()));

        return response;
    }
}
