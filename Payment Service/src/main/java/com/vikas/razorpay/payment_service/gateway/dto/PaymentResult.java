package com.vikas.razorpay.payment_service.gateway.dto;

public sealed interface PaymentResult permits PaymentResult.Pending,PaymentResult.Success
        , PaymentResult.Failure{

    record Pending(String registrationRef) implements PaymentResult {}


    record Failure(String error,String errorDescription) implements PaymentResult {}


    record Success(String bankReference) implements PaymentResult{}

}
