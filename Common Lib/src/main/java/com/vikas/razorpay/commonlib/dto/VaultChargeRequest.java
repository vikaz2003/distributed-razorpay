package com.vikas.razorpay.commonlib.dto;

import com.vikas.razorpay.commonlib.entity.Money;

import java.util.Map;
import java.util.UUID;

public record VaultChargeRequest(
        UUID paymentId,
        String token,
        Money amount,
        Map<String,Object> methodDetails
) {

}
