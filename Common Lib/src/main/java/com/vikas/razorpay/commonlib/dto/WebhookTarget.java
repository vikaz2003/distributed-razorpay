package com.vikas.razorpay.commonlib.dto;

import java.util.UUID;

public record WebhookTarget(
            UUID configId,
            String targetUrl,
            String webhookSecret
) {
}
