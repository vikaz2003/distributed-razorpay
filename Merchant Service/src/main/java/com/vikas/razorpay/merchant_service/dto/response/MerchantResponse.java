package com.vikas.razorpay.merchant_service.dto.response;


import com.vikas.razorpay.commonlib.enums.BusinessType;
import com.vikas.razorpay.commonlib.enums.MerchantStatus;

import java.util.UUID;


public record MerchantResponse(
        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus merchantStatus
) {


}
