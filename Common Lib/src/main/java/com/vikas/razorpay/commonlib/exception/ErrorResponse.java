package com.vikas.razorpay.commonlib.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        String errorCode,
        String errorDescription,
        LocalDateTime timestamp
) {

    public static ErrorResponse of(String errorCode,String errorDescription){
        return new ErrorResponse(errorCode,errorDescription,LocalDateTime.now());
    }
}
