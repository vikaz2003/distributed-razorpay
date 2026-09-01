package com.vikas.razorpay.merchant_service.service;


import com.vikas.razorpay.merchant_service.dto.request.CreateApiKeyRequest;
import com.vikas.razorpay.merchant_service.dto.response.ApiKeyCreateResponse;
import com.vikas.razorpay.merchant_service.dto.response.ApiKeyResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface ApiService {

    

     List<ApiKeyResponse> listByMerchant(UUID merchantId);

    void revoke(UUID merchantId, String keyId);

    ApiKeyCreateResponse rotate(UUID merchantId, String keyId);

    ApiKeyCreateResponse create(UUID merchantId, @Valid CreateApiKeyRequest request);
}
