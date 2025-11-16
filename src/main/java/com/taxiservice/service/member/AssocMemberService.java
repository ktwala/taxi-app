package com.taxiservice.service.member;

import com.taxiservice.dto.member.AssocMemberRequest;
import com.taxiservice.dto.member.AssocMemberResponse;
import com.taxiservice.entity.AssocMember;
import com.taxiservice.exception.DuplicateResourceException;
import com.taxiservice.exception.ResourceNotFoundException;
import com.taxiservice.repository.AssocMemberRepository;
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
public class AssocMemberService {

    private final AssocMemberRepository assocMemberRepository;

    public AssocMemberResponse createMember(AssocMemberRequest request, String currentUser) {
        log.info("Creating association member: {} by user: {}", request.getName(), currentUser);

        if (assocMemberRepository.existsBySquadNumber(request.getSquadNumber())) {
            throw new DuplicateResourceException("AssocMember", "squadNumber", request.getSquadNumber());
        }

        AssocMember member = AssocMember.builder()
                .name(request.getName())
                .contactNumber(request.getContactNumber())
                .squadNumber(request.getSquadNumber())
                .joinedAt(LocalDateTime.now())
                .blacklisted(request.getBlacklisted())
                .createdBy(currentUser)
                .build();

        AssocMember saved = assocMemberRepository.save(member);
        log.info("Association member created successfully with ID: {}", saved.getAssocMemberId());

        return convertToResponse(saved);
    }

    public AssocMemberResponse updateMember(Long memberId, AssocMemberRequest request, String currentUser) {
        log.info("Updating association member with ID: {} by user: {}", memberId, currentUser);

        AssocMember member = assocMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("AssocMember", "assocMemberId", memberId));

        if (!member.getSquadNumber().equals(request.getSquadNumber()) &&
                assocMemberRepository.existsBySquadNumber(request.getSquadNumber())) {
            throw new DuplicateResourceException("AssocMember", "squadNumber", request.getSquadNumber());
        }

        member.setName(request.getName());
        member.setContactNumber(request.getContactNumber());
        member.setSquadNumber(request.getSquadNumber());
        member.setBlacklisted(request.getBlacklisted());
        member.setUpdatedBy(currentUser);

        AssocMember updated = assocMemberRepository.save(member);
        log.info("Association member updated successfully");

        return convertToResponse(updated);
    }

    public void deleteMember(Long memberId) {
        log.info("Deleting association member with ID: {}", memberId);

        AssocMember member = assocMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("AssocMember", "assocMemberId", memberId));

        assocMemberRepository.delete(member);
        log.info("Association member deleted successfully");
    }

    public AssocMemberResponse blacklistMember(Long memberId, String reason, String currentUser) {
        log.info("Blacklisting member with ID: {} by user: {}. Reason: {}", memberId, currentUser, reason);

        AssocMember member = assocMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("AssocMember", "assocMemberId", memberId));

        member.setBlacklisted(true);
        member.setUpdatedBy(currentUser);

        AssocMember updated = assocMemberRepository.save(member);
        log.info("Member blacklisted successfully");

        return convertToResponse(updated);
    }

    public AssocMemberResponse removeBlacklist(Long memberId, String currentUser) {
        log.info("Removing blacklist for member with ID: {} by user: {}", memberId, currentUser);

        AssocMember member = assocMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("AssocMember", "assocMemberId", memberId));

        member.setBlacklisted(false);
        member.setUpdatedBy(currentUser);

        AssocMember updated = assocMemberRepository.save(member);
        log.info("Blacklist removed successfully");

        return convertToResponse(updated);
    }

    @Transactional(readOnly = true)
    public AssocMemberResponse getMemberById(Long memberId) {
        log.info("Fetching association member with ID: {}", memberId);

        AssocMember member = assocMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("AssocMember", "assocMemberId", memberId));

        return convertToResponse(member);
    }

    @Transactional(readOnly = true)
    public AssocMemberResponse getMemberBySquadNumber(String squadNumber) {
        log.info("Fetching association member by squad number: {}", squadNumber);

        AssocMember member = assocMemberRepository.findBySquadNumber(squadNumber)
                .orElseThrow(() -> new ResourceNotFoundException("AssocMember", "squadNumber", squadNumber));

        return convertToResponse(member);
    }

    @Transactional(readOnly = true)
    public List<AssocMemberResponse> getAllMembers() {
        log.info("Fetching all association members");

        return assocMemberRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AssocMemberResponse> getActiveMembers() {
        log.info("Fetching active association members");

        return assocMemberRepository.findAllActiveMembers().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AssocMemberResponse> getBlacklistedMembers() {
        log.info("Fetching blacklisted members");

        return assocMemberRepository.findByBlacklisted(true).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AssocMemberResponse> searchMembers(String name) {
        log.info("Searching members by name: {}", name);

        return assocMemberRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long countActiveMembers() {
        return assocMemberRepository.countActiveMembers();
    }

    private AssocMemberResponse convertToResponse(AssocMember member) {
        return AssocMemberResponse.builder()
                .assocMemberId(member.getAssocMemberId())
                .name(member.getName())
                .contactNumber(member.getContactNumber())
                .squadNumber(member.getSquadNumber())
                .joinedAt(member.getJoinedAt())
                .blacklisted(member.getBlacklisted())
                .createdBy(member.getCreatedBy())
                .updatedBy(member.getUpdatedBy())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
