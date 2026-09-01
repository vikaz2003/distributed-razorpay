package com.vikas.razorpay.payment_service.controller;


import com.vikas.razorpay.commonlib.dto.PaymentSettlementView;
import com.vikas.razorpay.payment_service.api.PaymentLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/payments")
@RequiredArgsConstructor
public class InternalController {


    private final PaymentLookupService paymentLookupService;


    @GetMapping("/unsettled-capture")
    public List<PaymentSettlementView> findUnSettledCaptured(@RequestParam UUID merchantId){
        return paymentLookupService.findUnSettledCaptured(merchantId);

    }

    @PostMapping("/mark-settled")
    public void markSettled(@RequestBody List<UUID> paymentIds){
        paymentLookupService.markSettled(paymentIds);
    }
}
