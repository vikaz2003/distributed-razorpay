package com.vikas.razorpay.merchant_service.service;


import com.vikas.razorpay.merchant_service.dto.request.UpdateWebhookConfigRequest;
import com.vikas.razorpay.merchant_service.dto.response.WebhookConfigResponse;

import java.util.List;
import java.util.UUID;

public interface WebhookConfigService {

    WebhookConfigResponse create(UUID merchantId, UpdateWebhookConfigRequest request);

    void delete(UUID merchantId,UUID configId);

    List<WebhookConfigResponse> list(UUID merchantId);

}
