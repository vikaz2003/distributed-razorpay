package com.vikas.razorpay.commonlib.exception;


import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException{


    private final String resource;

    public ResourceNotFoundException(String message, String resource) {
        super(message);
        this.resource = resource;
    }

}
