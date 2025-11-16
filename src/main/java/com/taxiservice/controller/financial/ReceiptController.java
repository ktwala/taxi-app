package com.taxiservice.controller.financial;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.financial.ReceiptRequest;
import com.taxiservice.dto.financial.ReceiptResponse;
import com.taxiservice.service.financial.ReceiptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
@Slf4j
public class ReceiptController {

    private final ReceiptService receiptService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReceiptResponse>> generateReceipt(@Valid @RequestBody ReceiptRequest request) {
        log.info("REST request to generate receipt for member ID: {}", request.getAssocMemberId());
        ReceiptResponse response = receiptService.generateReceipt(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Receipt generated successfully", response));
    }

    @GetMapping("/{receiptId}")
    public ResponseEntity<ApiResponse<ReceiptResponse>> getReceiptById(@PathVariable Long receiptId) {
        log.info("REST request to get receipt with ID: {}", receiptId);
        ReceiptResponse response = receiptService.getReceiptById(receiptId);
        return ResponseEntity.ok(ApiResponse.success("Receipt retrieved successfully", response));
    }

    @GetMapping("/number/{receiptNumber}")
    public ResponseEntity<ApiResponse<ReceiptResponse>> getReceiptByNumber(@PathVariable String receiptNumber) {
        log.info("REST request to get receipt by number: {}", receiptNumber);
        ReceiptResponse response = receiptService.getReceiptByNumber(receiptNumber);
        return ResponseEntity.ok(ApiResponse.success("Receipt retrieved successfully", response));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<ReceiptResponse>>> getReceiptsByMember(@PathVariable Long memberId) {
        log.info("REST request to get receipts for member ID: {}", memberId);
        List<ReceiptResponse> responses = receiptService.getReceiptsByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("Receipts retrieved successfully", responses));
    }

    @GetMapping("/issued-by/{issuedBy}")
    public ResponseEntity<ApiResponse<List<ReceiptResponse>>> getReceiptsByIssuedBy(@PathVariable String issuedBy) {
        log.info("REST request to get receipts issued by: {}", issuedBy);
        List<ReceiptResponse> responses = receiptService.getReceiptsByIssuedBy(issuedBy);
        return ResponseEntity.ok(ApiResponse.success("Receipts retrieved successfully", responses));
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<ReceiptResponse>>> getReceiptsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("REST request to get receipts between {} and {}", startDate, endDate);
        List<ReceiptResponse> responses = receiptService.getReceiptsByDateRange(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59));
        return ResponseEntity.ok(ApiResponse.success("Receipts retrieved successfully", responses));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReceiptResponse>>> getAllReceipts() {
        log.info("REST request to get all receipts");
        List<ReceiptResponse> responses = receiptService.getAllReceipts();
        return ResponseEntity.ok(ApiResponse.success("Receipts retrieved successfully", responses));
    }
}
