package com.vikas.razorpay.merchant_service.repo;

import com.vikas.razorpay.merchant_service.Entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {


    List<ApiKey> findByMerchant_Id(UUID merchantId);

    Optional<ApiKey> findByKeyId(String keyId);

    Optional<ApiKey> findByKeyIdAndMerchant_Id(String keyId,UUID merchantId);
}
