package com.taxiservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @NotNull(message = "Association member ID is required")
    @Column(name = "assoc_member_id", nullable = false)
    private Long assocMemberId;

    @NotBlank(message = "Message is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false, length = 10)
    private String status = "UNREAD";

    @NotBlank(message = "Notification type is required")
    @Column(name = "notification_type", nullable = false, length = 50)
    private String notificationType;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
