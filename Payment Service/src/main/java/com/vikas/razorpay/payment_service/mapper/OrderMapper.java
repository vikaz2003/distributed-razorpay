package com.vikas.razorpay.payment_service.mapper;


import com.vikas.razorpay.payment_service.dto.response.OrderResponse;
import com.vikas.razorpay.payment_service.entity.OrderRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(target = "status",source = "orderStatus")
    OrderResponse toResponse(OrderRecord order);
}
