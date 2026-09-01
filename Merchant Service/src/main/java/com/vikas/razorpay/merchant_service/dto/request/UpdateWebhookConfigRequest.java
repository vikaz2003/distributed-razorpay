package com.vikas.razorpay.merchant_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateWebhookConfigRequest(

        @NotBlank(message="Webhook URL is required")
        @Size(max=500)
        @Pattern(regexp = "^https?://.*",message = "Webhook URL must be a valid http URL")
        String targetUrl,

        // Comma -seperated fine-grained event type names
        // Null/blank/"ALL" subscribe to every event type
        @Size(max=1000)
        String eventTypes
) {
}
