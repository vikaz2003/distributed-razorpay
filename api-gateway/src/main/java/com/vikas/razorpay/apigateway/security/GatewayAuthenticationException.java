package com.vikas.razorpay.apigateway.security;


public class GatewayAuthenticationException extends RuntimeException{

    public GatewayAuthenticationException(String message) {
        super(message);
    }
}
