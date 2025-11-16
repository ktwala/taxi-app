package com.taxiservice.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long notificationId;
    private Long assocMemberId;
    private String memberName;
    private String message;
    private String status;
    private String notificationType;
    private LocalDateTime createdAt;
}
