package com.vikas.razorpay.payment_service.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.vikas.razorpay.commonlib.entity.Money;
import com.vikas.razorpay.commonlib.enums.PaymentMethod;
import com.vikas.razorpay.commonlib.enums.PaymentStatus;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentResponse(
         UUID id,
         UUID orderId,
         UUID merchantId,
         Money amount,
         PaymentStatus status,
         PaymentMethod method,
         Map<String,Object> methodDetails,
         String errorCode,
         String errorDescription,
         Long refundedAmountPaise,
         LocalDateTime capturedAt,
         LocalDateTime createdAt
) {
}
