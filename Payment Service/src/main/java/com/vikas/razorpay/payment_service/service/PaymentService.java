package com.vikas.razorpay.payment_service.service;


import com.vikas.razorpay.payment_service.dto.request.PaymentInItRequestDto;
import com.vikas.razorpay.payment_service.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse initiate(UUID merchantId, PaymentInItRequestDto request);

    PaymentResponse capture(UUID merchantId, UUID paymentId);

    void resolveAuthorization(UUID id, boolean b, String bankRef, String errorCode,String errorDescription);
}
