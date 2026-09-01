package com.vikas.razorpay.merchant_service.service.impl;


import com.vikas.razorpay.commonlib.exception.ResourceNotFoundException;
import com.vikas.razorpay.commonlib.util.RandomizerUtil;
import com.vikas.razorpay.merchant_service.Entity.Merchant;
import com.vikas.razorpay.merchant_service.Entity.MerchantWebhookConfig;
import com.vikas.razorpay.merchant_service.dto.request.UpdateWebhookConfigRequest;
import com.vikas.razorpay.merchant_service.dto.response.WebhookConfigResponse;
import com.vikas.razorpay.merchant_service.mapper.WebhookConfigMapper;
import com.vikas.razorpay.merchant_service.repo.MerchantRepository;
import com.vikas.razorpay.merchant_service.repo.WebhookConfigRepository;
import com.vikas.razorpay.merchant_service.service.WebhookConfigService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookConfigServiceImpl implements WebhookConfigService {

    private final WebhookConfigRepository webhookConfigRepository;
    private final MerchantRepository merchantRepository;
    private final BytesEncryptor bytesEncryptor;
    private final WebhookConfigMapper webhookConfigMapper;

    @Override
    public WebhookConfigResponse create(UUID merchantId, UpdateWebhookConfigRequest request) {
        Merchant merchant =merchantRepository.findById(merchantId)
                .orElseThrow(()-> new ResourceNotFoundException("Merchant not found with id: "+merchantId,"Merchant"));
        String rawSecret= RandomizerUtil.randomBase64(32);
        String encryptedSecret= Base64
                .getEncoder()
                .encodeToString(bytesEncryptor.encrypt(rawSecret.getBytes(StandardCharsets.UTF_8)));
        MerchantWebhookConfig merchantWebhookConfig=MerchantWebhookConfig.builder()
                .webhookSecret(encryptedSecret)
                .merchant(merchant)
                .enabled(true)
                .targetUrl(request.targetUrl())
                .eventTypes(request.eventTypes())
                .build();
        merchantWebhookConfig=webhookConfigRepository.save(merchantWebhookConfig);
        return webhookConfigMapper.toResponse(merchantWebhookConfig,rawSecret);
    }

    @Override
    @Transactional
    public void delete(UUID merchantId, UUID configId) {
        MerchantWebhookConfig merchantWebhookConfig=webhookConfigRepository.findByIdAndMerchant_Id(configId, merchantId)
                .orElseThrow(()-> new ResourceNotFoundException("Config not found with id: "+configId,"CONFIG"));
        merchantWebhookConfig.setEnabled(false);
        webhookConfigRepository.save(merchantWebhookConfig);
    }

    @Override
    public List<WebhookConfigResponse> list(UUID merchantId) {
        merchantRepository.findById(merchantId)
                .orElseThrow(()-> new ResourceNotFoundException("Merchant not found with id: "+merchantId,"Merchant"));
        List<MerchantWebhookConfig> merchantWebhookConfigs=webhookConfigRepository.findAllByMerchant_IdAndEnabledTrue(merchantId);
        return merchantWebhookConfigs.stream()
                .map(config -> webhookConfigMapper.toResponse(config, null))
                .toList();
    }



}
