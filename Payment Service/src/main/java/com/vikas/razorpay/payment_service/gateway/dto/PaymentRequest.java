package com.vikas.razorpay.payment_service.gateway.dto;



import com.vikas.razorpay.commonlib.entity.Money;
import com.vikas.razorpay.commonlib.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentRequest(
        UUID paymentId,
        UUID orderId,
        UUID merchantId,
        Money amount,
        PaymentMethod method,
        Map<String,Object> methodDetails
) {
}
