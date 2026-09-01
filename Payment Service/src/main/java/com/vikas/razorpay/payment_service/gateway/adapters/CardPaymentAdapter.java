package com.vikas.razorpay.payment_service.gateway.adapters;


import com.vikas.razorpay.commonlib.dto.VaultChargeRequest;
import com.vikas.razorpay.payment_service.clients.VaultServiceClient;
import com.vikas.razorpay.payment_service.gateway.PaymentAdapter;
import com.vikas.razorpay.payment_service.gateway.dto.PaymentRequest;
import com.vikas.razorpay.payment_service.gateway.dto.PaymentResult;
import com.vikas.razorpay.payment_service.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardPaymentAdapter implements PaymentAdapter {

    private final VaultServiceClient vaultServiceClient;

    @Override
    public PaymentResult initiate(PaymentRequest request) {
        String token=(String)request.methodDetails().get("token");
        PaymentProcessorResponse response=vaultServiceClient.charge(new VaultChargeRequest(request.paymentId(),token,request.amount(),request.methodDetails()));
        return switch(response){
            case  PaymentProcessorResponse.Success success-> new PaymentResult.Success(success.bankReference());
            case PaymentProcessorResponse.Failure failure-> new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
            case PaymentProcessorResponse.Pending pending-> new PaymentResult.Pending(pending.processorReference());
        };
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("CARD_REF");
    }
}
