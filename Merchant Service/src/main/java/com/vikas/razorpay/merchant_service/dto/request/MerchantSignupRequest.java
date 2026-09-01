package com.vikas.razorpay.merchant_service.dto.request;


import com.vikas.razorpay.commonlib.enums.BusinessType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MerchantSignupRequest(
        @NotNull(message="Name should be provided")
        @Size(max=50,message="Name should not be more then 50 characters long ")
        String name,
        @Email(message="Email is invalid")
        @NotNull(message = "Email is required")
        String email,
        @NotNull(message = "Password is required")
        @Size(min=8,message = "Password should be at least 8 characters long")
        String password,
        @Size(max=50,message = "BusinessName should not be more then 50 characters")
        String businessName,
        BusinessType businessType

) {
}
