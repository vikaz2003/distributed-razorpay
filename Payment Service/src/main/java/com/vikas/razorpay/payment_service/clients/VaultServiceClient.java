package com.vikas.razorpay.payment_service.clients;

import com.vikas.razorpay.commonlib.dto.VaultChargeRequest;
import com.vikas.razorpay.payment_service.processor.dto.PaymentProcessorResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="vault-service",path ="/internal/vault")
public interface VaultServiceClient {


    @PostMapping("/client")
    PaymentProcessorResponse charge(@RequestBody VaultChargeRequest request);

}
