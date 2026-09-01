package com.vikas.razorpay.merchant_service.dto.response;



import com.vikas.razorpay.commonlib.enums.Environment;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApiKeyResponse(
        UUID id,
        String keyId,
        Environment environment,
        boolean Enabled,
        LocalDateTime lastUsedAt
) {

}
