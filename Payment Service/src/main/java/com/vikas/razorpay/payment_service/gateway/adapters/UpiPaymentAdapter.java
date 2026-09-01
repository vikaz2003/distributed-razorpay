package com.vikas.razorpay.payment_service.gateway.adapters;


import com.vikas.razorpay.commonlib.enums.PaymentMethod;
import com.vikas.razorpay.payment_service.gateway.PaymentAdapter;
import com.vikas.razorpay.payment_service.gateway.dto.PaymentRequest;
import com.vikas.razorpay.payment_service.gateway.dto.PaymentResult;
import com.vikas.razorpay.payment_service.processor.PaymentProcessorRouter;
import com.vikas.razorpay.payment_service.processor.dto.PaymentProcessorRequest;
import com.vikas.razorpay.payment_service.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@Slf4j
@RequiredArgsConstructor
public class UpiPaymentAdapter implements PaymentAdapter {

    private final PaymentProcessorRouter paymentProcessorRouter;


    @Override
    public PaymentResult initiate(PaymentRequest request) {
        log.info("Initiate Payment with UPI,paymentId: {}",request.paymentId());

        try {
            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.nonCard(
                    request.paymentId(),
                    PaymentMethod.UPI,
                    request.amount(),
                    request.methodDetails()
            );
            PaymentProcessorResponse response = paymentProcessorRouter.charge(paymentProcessorRequest);

            return switch (response) {
                case PaymentProcessorResponse.Failure failure ->
                        new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
                case PaymentProcessorResponse.Pending pending ->
                        new PaymentResult.Pending(pending.processorReference());
                case PaymentProcessorResponse.Success success -> new PaymentResult.Success(success.bankReference());
            };
        } catch (Exception e) {
            log.warn("UPI Payment  failed for paymentId: {}",request.paymentId());
            return new PaymentResult.Failure("nbk_failed", e.getMessage());
        }
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("UPI_REF");
    }
}
