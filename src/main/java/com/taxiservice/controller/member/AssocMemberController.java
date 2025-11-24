package com.taxiservice.controller.member;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.member.AssocMemberRequest;
import com.taxiservice.dto.member.AssocMemberResponse;
import com.taxiservice.security.CustomUserDetails;
import com.taxiservice.service.member.AssocMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class AssocMemberController {

    private final AssocMemberService assocMemberService;

    @PostMapping
    public ResponseEntity<ApiResponse<AssocMemberResponse>> createMember(
            @Valid @RequestBody AssocMemberRequest request,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String currentUser = userDetails.getUsername();
        log.info("REST request to create association member: {} by user: {}", request.getName(), currentUser);
        AssocMemberResponse response = assocMemberService.createMember(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Member created successfully", response));
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<ApiResponse<AssocMemberResponse>> updateMember(
            @PathVariable Long memberId,
            @Valid @RequestBody AssocMemberRequest request,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String currentUser = userDetails.getUsername();
        log.info("REST request to update member with ID: {} by user: {}", memberId, currentUser);
        AssocMemberResponse response = assocMemberService.updateMember(memberId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Member updated successfully", response));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable Long memberId) {
        log.info("REST request to delete member with ID: {}", memberId);
        assocMemberService.deleteMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("Member deleted successfully", null));
    }

    @PatchMapping("/{memberId}/blacklist")
    public ResponseEntity<ApiResponse<AssocMemberResponse>> blacklistMember(
            @PathVariable Long memberId,
            @RequestParam String reason,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String currentUser = userDetails.getUsername();
        log.info("REST request to blacklist member with ID: {} by user: {}. Reason: {}", memberId, currentUser, reason);
        AssocMemberResponse response = assocMemberService.blacklistMember(memberId, reason, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Member blacklisted successfully", response));
    }

    @PatchMapping("/{memberId}/remove-blacklist")
    public ResponseEntity<ApiResponse<AssocMemberResponse>> removeBlacklist(
            @PathVariable Long memberId,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String currentUser = userDetails.getUsername();
        log.info("REST request to remove blacklist for member with ID: {} by user: {}", memberId, currentUser);
        AssocMemberResponse response = assocMemberService.removeBlacklist(memberId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Blacklist removed successfully", response));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<AssocMemberResponse>> getMemberById(@PathVariable Long memberId) {
        log.info("REST request to get member with ID: {}", memberId);
        AssocMemberResponse response = assocMemberService.getMemberById(memberId);
        return ResponseEntity.ok(ApiResponse.success("Member retrieved successfully", response));
    }

    @GetMapping("/squad/{squadNumber}")
    public ResponseEntity<ApiResponse<AssocMemberResponse>> getMemberBySquadNumber(@PathVariable String squadNumber) {
        log.info("REST request to get member by squad number: {}", squadNumber);
        AssocMemberResponse response = assocMemberService.getMemberBySquadNumber(squadNumber);
        return ResponseEntity.ok(ApiResponse.success("Member retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AssocMemberResponse>>> getAllMembers() {
        log.info("REST request to get all members");
        List<AssocMemberResponse> responses = assocMemberService.getAllMembers();
        return ResponseEntity.ok(ApiResponse.success("Members retrieved successfully", responses));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<AssocMemberResponse>>> getActiveMembers() {
        log.info("REST request to get active members");
        List<AssocMemberResponse> responses = assocMemberService.getActiveMembers();
        return ResponseEntity.ok(ApiResponse.success("Active members retrieved successfully", responses));
    }

    @GetMapping("/blacklisted")
    public ResponseEntity<ApiResponse<List<AssocMemberResponse>>> getBlacklistedMembers() {
        log.info("REST request to get blacklisted members");
        List<AssocMemberResponse> responses = assocMemberService.getBlacklistedMembers();
        return ResponseEntity.ok(ApiResponse.success("Blacklisted members retrieved successfully", responses));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AssocMemberResponse>>> searchMembers(@RequestParam String keyword) {
        log.info("REST request to search members with keyword: {}", keyword);
        List<AssocMemberResponse> responses = assocMemberService.searchMembers(keyword);
        return ResponseEntity.ok(ApiResponse.success("Members retrieved successfully", responses));
    }

    @GetMapping("/count/active")
    public ResponseEntity<ApiResponse<Long>> countActiveMembers() {
        log.info("REST request to count active members");
        Long count = assocMemberService.countActiveMembers();
        return ResponseEntity.ok(ApiResponse.success("Active member count retrieved", count));
    }
}
