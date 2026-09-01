package com.vikas.razorpay.merchant_service.service;


import com.vikas.razorpay.merchant_service.dto.request.LoginRequestDto;
import com.vikas.razorpay.merchant_service.dto.request.MerchantSignupRequest;
import com.vikas.razorpay.merchant_service.dto.response.LoginResponseDto;
import com.vikas.razorpay.merchant_service.dto.response.MerchantResponse;
import jakarta.validation.Valid;

public interface AuthService {

    MerchantResponse signup(MerchantSignupRequest merchantSignupRequest);

    LoginResponseDto login(@Valid LoginRequestDto requestDto);
}
