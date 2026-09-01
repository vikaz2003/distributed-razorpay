package com.vikas.razorpay.vaultservice.dto.response;


import com.vikas.razorpay.commonlib.enums.CardBrand;

public record TokenizeResponse(
        String token,
        String lastFour,
        CardBrand brand,
        Integer expiryMonth,
        Integer expiryYear
) {


}
