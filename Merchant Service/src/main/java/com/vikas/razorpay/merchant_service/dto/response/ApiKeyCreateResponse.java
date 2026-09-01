package com.vikas.razorpay.merchant_service.dto.response;



import com.vikas.razorpay.commonlib.enums.Environment;

import java.util.UUID;

public record ApiKeyCreateResponse(

        UUID id,
        String KeyId,
        String KeySecret,
        Environment environment

) {
}
