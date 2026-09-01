package com.vikas.razorpay.vaultservice.Mapper;



import com.vikas.razorpay.vaultservice.dto.response.TokenizeResponse;
import com.vikas.razorpay.vaultservice.entity.VaultCard;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VaultMapper {


    TokenizeResponse toTokenizeResponse(VaultCard vault);
}
