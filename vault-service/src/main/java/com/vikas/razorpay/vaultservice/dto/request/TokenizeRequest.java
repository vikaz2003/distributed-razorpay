package com.vikas.razorpay.vaultservice.dto.request;


import com.vikas.razorpay.vaultservice.validation.ExpiryYearValidator;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.LuhnCheck;

import java.util.UUID;

public record TokenizeRequest(

        @NotBlank(message = "PAN is required")
        @LuhnCheck(message = "Invalid card number")
        @Pattern(regexp = "^[0-9]{13,19}$",message = "Pan length is not correct")
        String pan,

        @NotBlank(message = "CVV is required")
        @Pattern(regexp = "^[0-9]{3,4}$",message = "CVV length is not correct ")
        String cvv,

        @NotNull(message = "Expiry month is required")
        @Max(value = 12,message = "expiry must be between 1 to 12")
        @Min(value = 1,message = "expiry must be between 1 to 12")
        Integer expiryMonth,

        @NotNull(message = "Expiry Year is required")
        @ExpiryYearValidator
        Integer expiryYear,

        UUID customerId,

        @Size(min=3,message = "Card Holder should have at least three digits")
        String cardHolderName
) {
}
