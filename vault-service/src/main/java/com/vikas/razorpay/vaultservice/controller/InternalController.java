package com.vikas.razorpay.vaultservice.controller;


import com.vikas.razorpay.commonlib.dto.PaymentProcessorResponse;
import com.vikas.razorpay.commonlib.dto.VaultChargeRequest;
import com.vikas.razorpay.vaultservice.service.VaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/vault")
@RequiredArgsConstructor
public class InternalController {

    private final VaultService vaultService;

   @PostMapping("/client")
   public PaymentProcessorResponse charge(@RequestBody VaultChargeRequest request){
       return vaultService.charge(request.paymentId(),request.token(),request.amount(),request.methodDetails());
   }


}
