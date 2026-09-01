package com.vikas.razorpay.vaultservice.service;


import com.vikas.razorpay.commonlib.dto.PaymentProcessorResponse;
import com.vikas.razorpay.commonlib.entity.Money;
import com.vikas.razorpay.vaultservice.dto.request.TokenizeRequest;
import com.vikas.razorpay.vaultservice.dto.response.TokenizeResponse;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

public interface VaultService {

    TokenizeResponse tokenize(@Valid TokenizeRequest request, UUID merchantId);

    PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails);
}
