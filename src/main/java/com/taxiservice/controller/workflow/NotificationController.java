package com.taxiservice.controller.workflow;

import com.taxiservice.dto.common.ApiResponse;
import com.taxiservice.dto.common.NotificationRequest;
import com.taxiservice.dto.common.NotificationResponse;
import com.taxiservice.service.workflow.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> sendNotification(@Valid @RequestBody NotificationRequest request) {
        log.info("REST request to send notification to member ID: {}", request.getAssocMemberId());
        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notification sent successfully", response));
    }

    @PatchMapping("/{notificationId}/mark-read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable Long notificationId) {
        log.info("REST request to mark notification ID {} as read", notificationId);
        NotificationResponse response = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", response));
    }

    @PatchMapping("/member/{memberId}/mark-all-read")
    public ResponseEntity<ApiResponse<Void>> markAllAsReadForMember(@PathVariable Long memberId) {
        log.info("REST request to mark all notifications as read for member ID: {}", memberId);
        notificationService.markAllAsReadForMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }

    @PostMapping("/payment-reminder/{memberId}")
    public ResponseEntity<ApiResponse<NotificationResponse>> sendPaymentReminder(@PathVariable Long memberId) {
        log.info("REST request to send payment reminder to member ID: {}", memberId);
        NotificationResponse response = notificationService.sendPaymentReminder(memberId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment reminder sent", response));
    }

    @PostMapping("/fine-notice")
    public ResponseEntity<ApiResponse<NotificationResponse>> sendFineNotice(
            @RequestParam Long memberId,
            @RequestParam String fineReason) {
        log.info("REST request to send fine notice to member ID: {} for reason: {}", memberId, fineReason);
        NotificationResponse response = notificationService.sendFineNotice(memberId, fineReason);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Fine notice sent", response));
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotificationById(@PathVariable Long notificationId) {
        log.info("REST request to get notification with ID: {}", notificationId);
        NotificationResponse response = notificationService.getNotificationById(notificationId);
        return ResponseEntity.ok(ApiResponse.success("Notification retrieved successfully", response));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByMember(@PathVariable Long memberId) {
        log.info("REST request to get notifications for member ID: {}", memberId);
        List<NotificationResponse> responses = notificationService.getNotificationsByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", responses));
    }

    @GetMapping("/member/{memberId}/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadByMember(@PathVariable Long memberId) {
        log.info("REST request to get unread notifications for member ID: {}", memberId);
        List<NotificationResponse> responses = notificationService.getUnreadByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("Unread notifications retrieved", responses));
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAllUnreadNotifications() {
        log.info("REST request to get all unread notifications");
        List<NotificationResponse> responses = notificationService.getAllUnreadNotifications();
        return ResponseEntity.ok(ApiResponse.success("Unread notifications retrieved", responses));
    }

    @GetMapping("/member/{memberId}/count-unread")
    public ResponseEntity<ApiResponse<Long>> countUnreadByMember(@PathVariable Long memberId) {
        log.info("REST request to count unread notifications for member ID: {}", memberId);
        Long count = notificationService.countUnreadByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success("Unread count retrieved", count));
    }
}
