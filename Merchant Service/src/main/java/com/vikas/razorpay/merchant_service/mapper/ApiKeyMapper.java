package com.vikas.razorpay.merchant_service.mapper;


import com.vikas.razorpay.merchant_service.Entity.ApiKey;
import com.vikas.razorpay.merchant_service.dto.response.ApiKeyCreateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApiKeyMapper {


    ApiKeyCreateResponse toCreateResponse(ApiKey apiKey);

}
