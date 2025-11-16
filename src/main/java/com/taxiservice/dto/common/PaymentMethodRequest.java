package com.taxiservice.dto.common;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodRequest {

    @NotBlank(message = "Method name is required")
    private String methodName;

    private String description;

    private Boolean active;
}
