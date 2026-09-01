package com.vikas.razorpay.merchant_service.service.impl;


import com.vikas.razorpay.commonlib.exception.ResourceNotFoundException;
import com.vikas.razorpay.commonlib.util.RandomizerUtil;
import com.vikas.razorpay.merchant_service.Entity.ApiKey;
import com.vikas.razorpay.merchant_service.Entity.Merchant;
import com.vikas.razorpay.merchant_service.cache.ApiKeyCache;
import com.vikas.razorpay.merchant_service.dto.request.CreateApiKeyRequest;
import com.vikas.razorpay.merchant_service.dto.response.ApiKeyCreateResponse;
import com.vikas.razorpay.merchant_service.dto.response.ApiKeyResponse;
import com.vikas.razorpay.merchant_service.mapper.ApiKeyMapper;
import com.vikas.razorpay.merchant_service.repo.ApiKeyRepository;
import com.vikas.razorpay.merchant_service.repo.AppUserRepository;
import com.vikas.razorpay.merchant_service.repo.MerchantRepository;
import com.vikas.razorpay.merchant_service.service.ApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiServiceImpl implements ApiService {


    private final MerchantRepository merchantRepository;
    private final AppUserRepository appUserRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyMapper apiKeyMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApiKeyCache apiKeyCache;

    @Override
    public List<ApiKeyResponse> listByMerchant(UUID merchantId) {
       return apiKeyRepository.findByMerchant_Id(merchantId)
               .stream()
               .map(apiKey -> new ApiKeyResponse(apiKey.getId(),
               apiKey.getKeyId(),apiKey.getEnvironment(), apiKey.isEnabled(),apiKey.getLastUsedAt()))
               .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public void revoke(UUID merchantId, String keyId) {
        ApiKey key=apiKeyRepository.findByKeyId(keyId)
                .filter(k -> k.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("ApiKey","API KEY"));
        key.setEnabled(false);
        apiKeyCache.evict(keyId);
    }

    @Override
    @Transactional
    public ApiKeyCreateResponse rotate(UUID merchantId, String keyId) {
        ApiKey key=apiKeyRepository.findByKeyIdAndMerchant_Id(keyId,merchantId
                )
                .orElseThrow(() -> new ResourceNotFoundException("ApiKeyNot found with keyId: "+keyId,"ApiKey"));

        String newRawSecret= RandomizerUtil.randomBase64(40);
        key.setPreviousKeySecretHash(key.getKeySecretHash());
        key.setKeySecretHash(passwordEncoder.encode(newRawSecret));
        key.setRotatedAt(LocalDateTime.now());
        key.setGracePeriodExpiresAt(LocalDateTime.now().plusHours(24));
        apiKeyCache.evict(keyId);
        key=apiKeyRepository.save(key);
        return new ApiKeyCreateResponse(key.getId(),key.getKeyId(),newRawSecret,key.getEnvironment());
    }

    @Override
    @Transactional
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request) {
         Merchant merchant=merchantRepository.findById(merchantId).orElseThrow(()-> new ResourceNotFoundException("No Resource Found for","merchant"));
         String keyId="rzp_"+request.getEnvironment().name().toLowerCase()+RandomizerUtil.randomBase64(24);
         String rawSecret=RandomizerUtil.randomBase64(40); // TODO : replace with cryptographic secret
         ApiKey key=ApiKey.builder()
                 .merchant(merchant)
                 .keyId(keyId)
                 .keySecretHash(passwordEncoder.encode(rawSecret))
                 .environment(request.getEnvironment())
                 .build();
          ApiKey savedApiKey=apiKeyRepository.save(key);
          return new ApiKeyCreateResponse(savedApiKey.getId(), savedApiKey.getKeyId(), rawSecret, savedApiKey.getEnvironment());

    }


}
