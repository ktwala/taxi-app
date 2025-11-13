package com.taxiservice.service.member;

import com.taxiservice.dto.member.MemberFinanceRequest;
import com.taxiservice.dto.member.MemberFinanceResponse;
import com.taxiservice.entity.MemberFinance;
import com.taxiservice.exception.DuplicateResourceException;
import com.taxiservice.exception.ResourceNotFoundException;
import com.taxiservice.repository.AssocMemberRepository;
import com.taxiservice.repository.MemberFinanceRepository;
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
public class MemberFinanceService {

    private final MemberFinanceRepository memberFinanceRepository;
    private final AssocMemberRepository assocMemberRepository;

    public MemberFinanceResponse createMemberFinance(MemberFinanceRequest request) {
        log.info("Creating member finance record for member ID: {}", request.getAssocMemberId());

        // Verify member exists
        assocMemberRepository.findById(request.getAssocMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("AssocMember", "assocMemberId", request.getAssocMemberId()));

        // Check if finance record already exists for this member
        if (memberFinanceRepository.findByAssocMemberId(request.getAssocMemberId()).isPresent()) {
            throw new DuplicateResourceException("MemberFinance", "assocMemberId", request.getAssocMemberId());
        }

        MemberFinance finance = MemberFinance.builder()
                .assocMemberId(request.getAssocMemberId())
                .joiningFeeAmount(request.getJoiningFeeAmount())
                .joiningFeePaid(request.getJoiningFeePaid())
                .membershipCardIssued(request.getMembershipCardIssued())
                .build();

        MemberFinance saved = memberFinanceRepository.save(finance);
        log.info("Member finance record created successfully with ID: {}", saved.getFinanceId());

        return convertToResponse(saved);
    }

    public MemberFinanceResponse recordJoiningFeePayment(Long financeId) {
        log.info("Recording joining fee payment for finance ID: {}", financeId);

        MemberFinance finance = memberFinanceRepository.findById(financeId)
                .orElseThrow(() -> new ResourceNotFoundException("MemberFinance", "financeId", financeId));

        if (finance.getJoiningFeePaid()) {
            throw new IllegalStateException("Joining fee has already been paid");
        }

        finance.setJoiningFeePaid(true);

        MemberFinance updated = memberFinanceRepository.save(finance);
        log.info("Joining fee payment recorded successfully");

        return convertToResponse(updated);
    }

    public MemberFinanceResponse issueMembershipCard(Long financeId) {
        log.info("Issuing membership card for finance ID: {}", financeId);

        MemberFinance finance = memberFinanceRepository.findById(financeId)
                .orElseThrow(() -> new ResourceNotFoundException("MemberFinance", "financeId", financeId));

        if (!finance.getJoiningFeePaid()) {
            throw new IllegalStateException("Cannot issue membership card before joining fee is paid");
        }

        if (finance.getMembershipCardIssued()) {
            throw new IllegalStateException("Membership card has already been issued");
        }

        finance.setMembershipCardIssued(true);

        MemberFinance updated = memberFinanceRepository.save(finance);
        log.info("Membership card issued successfully");

        return convertToResponse(updated);
    }

    @Transactional(readOnly = true)
    public MemberFinanceResponse getFinanceById(Long financeId) {
        log.info("Fetching member finance with ID: {}", financeId);

        MemberFinance finance = memberFinanceRepository.findById(financeId)
                .orElseThrow(() -> new ResourceNotFoundException("MemberFinance", "financeId", financeId));

        return convertToResponse(finance);
    }

    @Transactional(readOnly = true)
    public MemberFinanceResponse getFinanceByMemberId(Long memberId) {
        log.info("Fetching member finance for member ID: {}", memberId);

        MemberFinance finance = memberFinanceRepository.findByAssocMemberId(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("MemberFinance", "assocMemberId", memberId));

        return convertToResponse(finance);
    }

    @Transactional(readOnly = true)
    public List<MemberFinanceResponse> getAllMemberFinances() {
        log.info("Fetching all member finances");

        return memberFinanceRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MemberFinanceResponse> getPendingJoiningFees() {
        log.info("Fetching pending joining fees");

        return memberFinanceRepository.findPendingJoiningFees().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MemberFinanceResponse> getPendingMembershipCards() {
        log.info("Fetching pending membership cards");

        return memberFinanceRepository.findPendingMembershipCards().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private MemberFinanceResponse convertToResponse(MemberFinance finance) {
        MemberFinanceResponse response = MemberFinanceResponse.builder()
                .financeId(finance.getFinanceId())
                .assocMemberId(finance.getAssocMemberId())
                .joiningFeePaid(finance.getJoiningFeePaid())
                .joiningFeeAmount(finance.getJoiningFeeAmount())
                .membershipCardIssued(finance.getMembershipCardIssued())
                .createdAt(finance.getCreatedAt())
                .updatedAt(finance.getUpdatedAt())
                .build();

        // Fetch member name
        assocMemberRepository.findById(finance.getAssocMemberId())
                .ifPresent(member -> response.setMemberName(member.getName()));

        return response;
    }
}
