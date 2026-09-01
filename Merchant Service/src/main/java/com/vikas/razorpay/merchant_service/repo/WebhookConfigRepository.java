package com.vikas.razorpay.merchant_service.repo;


import com.vikas.razorpay.merchant_service.Entity.MerchantWebhookConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebhookConfigRepository extends JpaRepository<MerchantWebhookConfig, UUID> {

    List<MerchantWebhookConfig> findAllByMerchant_IdAndEnabledTrue(UUID merchantId);

    Optional<MerchantWebhookConfig> findByIdAndMerchant_Id(UUID configId, UUID merchantId);

    List<MerchantWebhookConfig> findByMerchant_IdAndEnabledTrue(UUID merchantId);
}
