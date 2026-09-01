package com.vikas.razorpay.commonlib.exception;

public class IdempotencyConflictException extends RuntimeException{

    public IdempotencyConflictException(String message) {
        super(message);
    }
}
