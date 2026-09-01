package com.vikas.razorpay.payment_service.processor;


import com.vikas.razorpay.payment_service.processor.dto.PaymentProcessorRequest;
import com.vikas.razorpay.payment_service.processor.dto.PaymentProcessorResponse;

public interface PaymentProcessor {

    PaymentProcessorResponse charge(PaymentProcessorRequest request);
}
