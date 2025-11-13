package com.taxiservice.service.workflow;

import com.taxiservice.dto.common.NotificationRequest;
import com.taxiservice.dto.common.NotificationResponse;
import com.taxiservice.entity.Notification;
import com.taxiservice.exception.ResourceNotFoundException;
import com.taxiservice.repository.AssocMemberRepository;
import com.taxiservice.repository.NotificationRepository;
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
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AssocMemberRepository assocMemberRepository;

    public NotificationResponse sendNotification(NotificationRequest request) {
        log.info("Sending notification to member ID: {} of type: {}", request.getAssocMemberId(), request.getNotificationType());

        // Verify member exists
        assocMemberRepository.findById(request.getAssocMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("AssocMember", "assocMemberId", request.getAssocMemberId()));

        Notification notification = Notification.builder()
                .assocMemberId(request.getAssocMemberId())
                .message(request.getMessage())
                .notificationType(request.getNotificationType())
                .status("UNREAD")
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Notification sent successfully with ID: {}", saved.getNotificationId());

        return convertToResponse(saved);
    }

    public NotificationResponse markAsRead(Long notificationId) {
        log.info("Marking notification ID: {} as read", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "notificationId", notificationId));

        notification.setStatus("READ");

        Notification updated = notificationRepository.save(notification);
        log.info("Notification marked as read");

        return convertToResponse(updated);
    }

    public void markAllAsReadForMember(Long memberId) {
        log.info("Marking all notifications as read for member ID: {}", memberId);

        List<Notification> unreadNotifications = notificationRepository.findUnreadByMember(memberId);
        unreadNotifications.forEach(notification -> notification.setStatus("READ"));

        notificationRepository.saveAll(unreadNotifications);
        log.info("Marked {} notifications as read", unreadNotifications.size());
    }

    public NotificationResponse sendPaymentReminder(Long memberId) {
        log.info("Sending payment reminder to member ID: {}", memberId);

        NotificationRequest request = NotificationRequest.builder()
                .assocMemberId(memberId)
                .message("Reminder: You have outstanding levy payments. Please settle your account.")
                .notificationType("Payment Reminder")
                .build();

        return sendNotification(request);
    }

    public NotificationResponse sendFineNotice(Long memberId, String fineReason) {
        log.info("Sending fine notice to member ID: {}", memberId);

        NotificationRequest request = NotificationRequest.builder()
                .assocMemberId(memberId)
                .message("A fine has been issued to you. Reason: " + fineReason)
                .notificationType("Fine Notice")
                .build();

        return sendNotification(request);
    }

    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long notificationId) {
        log.info("Fetching notification with ID: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "notificationId", notificationId));

        return convertToResponse(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByMember(Long memberId) {
        log.info("Fetching notifications for member ID: {}", memberId);

        return notificationRepository.findByAssocMemberId(memberId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadByMember(Long memberId) {
        log.info("Fetching unread notifications for member ID: {}", memberId);

        return notificationRepository.findUnreadByMember(memberId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllUnreadNotifications() {
        log.info("Fetching all unread notifications");

        return notificationRepository.findAllUnread().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long countUnreadByMember(Long memberId) {
        return notificationRepository.countUnreadByMember(memberId);
    }

    private NotificationResponse convertToResponse(Notification notification) {
        NotificationResponse response = NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .assocMemberId(notification.getAssocMemberId())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .notificationType(notification.getNotificationType())
                .createdAt(notification.getCreatedAt())
                .build();

        // Fetch member name
        assocMemberRepository.findById(notification.getAssocMemberId())
                .ifPresent(member -> response.setMemberName(member.getName()));

        return response;
    }
}
