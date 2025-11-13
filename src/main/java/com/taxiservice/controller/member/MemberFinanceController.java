package com.taxiservice.controller.member;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.member.MemberFinanceRequest;
import com.taxiservice.dto.member.MemberFinanceResponse;
import com.taxiservice.service.member.MemberFinanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/member-finances")
@RequiredArgsConstructor
@Slf4j
public class MemberFinanceController {

    private final MemberFinanceService financeService;

    @PostMapping
    public ResponseEntity<ApiResponse<MemberFinanceResponse>> createMemberFinance(@Valid @RequestBody MemberFinanceRequest request) {
        log.info("REST request to create member finance for member ID: {}", request.getAssocMemberId());
        MemberFinanceResponse response = financeService.createMemberFinance(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Member finance created successfully", response));
    }

    @PatchMapping("/{financeId}/joining-fee")
    public ResponseEntity<ApiResponse<MemberFinanceResponse>> recordJoiningFeePayment(
            @PathVariable Long financeId,
            @RequestParam BigDecimal amount,
            @RequestParam String currentUser) {
        log.info("REST request to record joining fee payment for finance ID: {}", financeId);
        MemberFinanceResponse response = financeService.recordJoiningFeePayment(financeId, amount, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Joining fee payment recorded", response));
    }

    @PatchMapping("/{financeId}/membership-card")
    public ResponseEntity<ApiResponse<MemberFinanceResponse>> issueMembershipCard(
            @PathVariable Long financeId,
            @RequestParam String cardNumber,
            @RequestParam String currentUser) {
        log.info("REST request to issue membership card for finance ID: {}", financeId);
        MemberFinanceResponse response = financeService.issueMembershipCard(financeId, cardNumber, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Membership card issued successfully", response));
    }

    @GetMapping("/{financeId}")
    public ResponseEntity<ApiResponse<MemberFinanceResponse>> getFinanceById(@PathVariable Long financeId) {
        log.info("REST request to get finance with ID: {}", financeId);
        MemberFinanceResponse response = financeService.getFinanceById(financeId);
        return ResponseEntity.ok(ApiResponse.success("Finance retrieved successfully", response));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<MemberFinanceResponse>> getFinanceByMemberId(@PathVariable Long memberId) {
        log.info("REST request to get finance for member ID: {}", memberId);
        MemberFinanceResponse response = financeService.getFinanceByMemberId(memberId);
        return ResponseEntity.ok(ApiResponse.success("Finance retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberFinanceResponse>>> getAllMemberFinances() {
        log.info("REST request to get all member finances");
        List<MemberFinanceResponse> responses = financeService.getAllMemberFinances();
        return ResponseEntity.ok(ApiResponse.success("Finances retrieved successfully", responses));
    }

    @GetMapping("/pending/joining-fee")
    public ResponseEntity<ApiResponse<List<MemberFinanceResponse>>> getPendingJoiningFees() {
        log.info("REST request to get pending joining fees");
        List<MemberFinanceResponse> responses = financeService.getPendingJoiningFees();
        return ResponseEntity.ok(ApiResponse.success("Pending joining fees retrieved", responses));
    }

    @GetMapping("/pending/membership-card")
    public ResponseEntity<ApiResponse<List<MemberFinanceResponse>>> getPendingMembershipCards() {
        log.info("REST request to get pending membership cards");
        List<MemberFinanceResponse> responses = financeService.getPendingMembershipCards();
        return ResponseEntity.ok(ApiResponse.success("Pending membership cards retrieved", responses));
    }
}
