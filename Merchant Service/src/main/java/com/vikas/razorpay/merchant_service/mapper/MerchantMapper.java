package com.vikas.razorpay.merchant_service.mapper;


import com.vikas.razorpay.merchant_service.Entity.Merchant;
import com.vikas.razorpay.merchant_service.dto.request.MerchantSignupRequest;
import com.vikas.razorpay.merchant_service.dto.response.MerchantResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantMapper {

    Merchant toEntityFromSignUpRequest(MerchantSignupRequest request);
    @Mapping(source = "status",target = "merchantStatus")
    MerchantResponse toResponse(Merchant merchant);

}
