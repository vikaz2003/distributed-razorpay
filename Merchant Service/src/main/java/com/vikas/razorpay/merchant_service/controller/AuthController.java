package com.vikas.razorpay.merchant_service.controller;



import com.vikas.razorpay.merchant_service.dto.request.LoginRequestDto;
import com.vikas.razorpay.merchant_service.dto.request.MerchantSignupRequest;
import com.vikas.razorpay.merchant_service.dto.response.LoginResponseDto;
import com.vikas.razorpay.merchant_service.dto.response.MerchantResponse;
import com.vikas.razorpay.merchant_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {


    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MerchantResponse> signup(@RequestBody @Valid MerchantSignupRequest merchantSignupRequest){
           return ResponseEntity.status(201).body(authService.signup(merchantSignupRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto requestDto){
        return ResponseEntity.status(201).body(authService.login(requestDto));
    }

}
