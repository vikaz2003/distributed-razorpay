package com.vikas.razorpay.payment_service.mapper;


import com.vikas.razorpay.payment_service.dto.response.PaymentResponse;
import com.vikas.razorpay.payment_service.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    @Mapping(target = "orderId",source="order.id")
    @Mapping(target = "refundedAmountPaise", ignore = true)
    PaymentResponse toResponse(Payment payment);

    List<PaymentResponse> toResponseList(List<Payment> paymentList);


}
