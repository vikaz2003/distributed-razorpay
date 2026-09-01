package com.vikas.razorpay.merchant_service.service.impl;

import com.vikas.razorpay.commonlib.dto.SettlementBankDetails;
import com.vikas.razorpay.commonlib.dto.WebhookTarget;
import com.vikas.razorpay.commonlib.enums.MerchantStatus;
import com.vikas.razorpay.commonlib.exception.ResourceNotFoundException;
import com.vikas.razorpay.merchant_service.Entity.Merchant;
import com.vikas.razorpay.merchant_service.Entity.MerchantWebhookConfig;
import com.vikas.razorpay.merchant_service.api.MerchantLookupService;
import com.vikas.razorpay.merchant_service.repo.MerchantRepository;
import com.vikas.razorpay.merchant_service.repo.WebhookConfigRepository;
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
public class MerchantLookupServiceImpl implements MerchantLookupService {


    private WebhookConfigRepository webhookConfigRepository;
    private final BytesEncryptor bytesEncryptor;
    private final MerchantRepository merchantRepository;

    @Override
    public List<WebhookTarget> getActiveConfigForEvent(UUID merchantId, String eventType) {
        List<MerchantWebhookConfig> merchantWebhookConfigs=webhookConfigRepository.findByMerchant_IdAndEnabledTrue(merchantId);
        return merchantWebhookConfigs.stream().filter(config-> config.isSubscribedTo(eventType))
                .map(config-> {
                    byte[] encryptedSecretBytes = Base64.getDecoder().decode(config.getWebhookSecret());
                    byte[] decryptedSecretBytes = bytesEncryptor.decrypt(encryptedSecretBytes);
                    return new WebhookTarget(config.getId(),config.getTargetUrl(),
                            new String(decryptedSecretBytes, StandardCharsets.UTF_8));
                })
                .toList();


    }

    @Override
    public List<UUID> listActiveMerchantIds() {
        return merchantRepository.findByStatus(MerchantStatus.ACTIVE).stream().map(merchant -> merchant.getId()).toList();
    }

    @Override
    public SettlementBankDetails getSettlementBankDetails(UUID merchantId) {
        Merchant merchant=merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant Not found with id: "+merchantId,"Merchant"));
        return new SettlementBankDetails(merchant.getSettlementBankAccount(),merchant.getSettlementBankIfsc(),merchant.getSettlementBankAccountHolderName());
    }
}
