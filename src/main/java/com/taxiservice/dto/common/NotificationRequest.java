package com.taxiservice.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

    @NotNull(message = "Association member ID is required")
    private Long assocMemberId;

    @NotBlank(message = "Message is required")
    private String message;

    @NotBlank(message = "Notification type is required")
    private String notificationType; // Payment Reminder, System Alert, Fine Notice, etc.
}
