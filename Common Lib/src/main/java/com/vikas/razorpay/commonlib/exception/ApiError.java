package com.vikas.razorpay.commonlib.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        String errorCode,
        String errorDescription,
        LocalDateTime timestamp,
        List<FieldError> fieldErrors
) {

    public record FieldError(String field,String message){

    }

    public static ApiError of(String errorCode,String errorDescription){
        return new ApiError(errorCode,errorDescription,LocalDateTime.now(),null);
    }

    public static ApiError of(String errorCode, String errorDescription, List<FieldError> fieldErrors){
        return new ApiError(errorCode,errorDescription,LocalDateTime.now(),fieldErrors);
    }
}
