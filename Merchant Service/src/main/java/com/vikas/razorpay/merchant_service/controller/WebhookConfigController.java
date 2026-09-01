package com.vikas.razorpay.merchant_service.controller;


import com.vikas.razorpay.commonlib.context.MerchantContext;
import com.vikas.razorpay.merchant_service.dto.request.UpdateWebhookConfigRequest;
import com.vikas.razorpay.merchant_service.dto.response.WebhookConfigResponse;
import com.vikas.razorpay.merchant_service.service.WebhookConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/merchants/webhooks")
@RequiredArgsConstructor
public class WebhookConfigController {

    private final WebhookConfigService webhookConfigService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<WebhookConfigResponse> create(@Valid @RequestBody UpdateWebhookConfigRequest request){
        return ResponseEntity.ok(webhookConfigService.create(merchantContext.getMerchantId(),request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        webhookConfigService.delete(merchantContext.getMerchantId(),id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<WebhookConfigResponse>> list(){
        return ResponseEntity.ok(webhookConfigService.list(merchantContext.getMerchantId()));
    }



}
