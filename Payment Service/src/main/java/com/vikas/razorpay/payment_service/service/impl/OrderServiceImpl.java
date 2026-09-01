package com.vikas.razorpay.payment_service.service.impl;


import com.vikas.razorpay.commonlib.dto.FindOrCreateCustomerRequest;
import com.vikas.razorpay.commonlib.enums.EventAggregateType;
import com.vikas.razorpay.commonlib.enums.OrderStatus;
import com.vikas.razorpay.commonlib.exception.BusinessRuleViolationException;
import com.vikas.razorpay.commonlib.exception.DuplicateResourceException;
import com.vikas.razorpay.commonlib.exception.ResourceNotFoundException;
import com.vikas.razorpay.payment_service.clients.CustomerServiceClient;
import com.vikas.razorpay.payment_service.dto.request.CreateOrderRequest;
import com.vikas.razorpay.payment_service.dto.response.OrderResponse;
import com.vikas.razorpay.payment_service.dto.response.PaymentResponse;
import com.vikas.razorpay.payment_service.entity.OrderRecord;
import com.vikas.razorpay.payment_service.entity.Payment;
import com.vikas.razorpay.payment_service.mapper.OrderMapper;
import com.vikas.razorpay.payment_service.mapper.PaymentMapper;
import com.vikas.razorpay.payment_service.outbox.OutBoxEventPublisher;
import com.vikas.razorpay.payment_service.repository.OrderRepository;
import com.vikas.razorpay.payment_service.repository.PaymentRepository;
import com.vikas.razorpay.payment_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {


    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;
    private final CustomerServiceClient customerServiceClient;
    private final OutBoxEventPublisher outBoxEventPublisher;

    @Override
    @Transactional
    public OrderResponse create(UUID merchantId, CreateOrderRequest createOrderRequest) {
        if(createOrderRequest.receipt()!=null && orderRepository.existsByMerchantIdAndReceipt(merchantId, createOrderRequest.receipt())){
            throw new DuplicateResourceException("ORDER_RECEIPT_DUPLICATE","order cannot be duplicate");
        }

        UUID customerId=null;
        if(createOrderRequest.customer()!=null){
             customerId= customerServiceClient.findOrCreate(
                     new FindOrCreateCustomerRequest(
                             merchantId,
                             createOrderRequest.customer().email(),
                             createOrderRequest.customer().name(),
                             createOrderRequest.customer().phone())
             );




        }

        OrderRecord orderRecord= OrderRecord.builder()
                .receipt(createOrderRequest.receipt())
                .amount(createOrderRequest.amount())
                .notes(createOrderRequest.notes())
                .merchantId(merchantId)
                .orderStatus(OrderStatus.CREATED)
                .expiresAt(createOrderRequest.expiresAt()!=null ?createOrderRequest.expiresAt() :LocalDateTime.now().plusMinutes(30))
                .customerId(customerId)
                .build();


        orderRecord=orderRepository.save(orderRecord);



        outBoxEventPublisher.publish(EventAggregateType.ORDER,orderRecord.getId(),"ORDER_CREATED", Map.of("orderId",orderRecord.getId(),
                "merchantId",merchantId.toString(),
                "amountUnits",orderRecord.getAmount().getAmountUnits(),
                "amountCurrency",orderRecord.getAmount().getCurrency(),
                "status",orderRecord.getOrderStatus().name()));


        return orderMapper.toResponse(orderRecord);
    }

    @Override
    public OrderResponse getById(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord=
                orderRepository.findByIdAndMerchantId(orderId,merchantId).orElseThrow(()->new ResourceNotFoundException("Order Not Present with orderId: "+orderId,"Order"));
        return orderMapper.toResponse(orderRecord);
    }

    @Override
    @Transactional
    public OrderResponse cancel(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord=
                orderRepository.findByIdAndMerchantId(orderId,merchantId).orElseThrow(()->new ResourceNotFoundException("Order Not Present with orderId: "+orderId,"Order"));
        if(orderRecord.getOrderStatus()==OrderStatus.CANCELLED|| orderRecord.getOrderStatus()==OrderStatus.PAID){
             throw new BusinessRuleViolationException("Cannot cancel order with status: "+ orderRecord.getOrderStatus(),"ORDER_CANNOT_CANCEL");
        }
        orderRecord.setOrderStatus(OrderStatus.CANCELLED);
        orderRecord=orderRepository.save(orderRecord);


        outBoxEventPublisher.publish(EventAggregateType.ORDER,orderRecord.getId(),"ORDER_CANCELLED", Map.of("orderId",orderRecord.getId(),
                "merchantId",merchantId.toString(),
                "amountUnits",orderRecord.getAmount().getAmountUnits(),
                "amountCurrency",orderRecord.getAmount().getCurrency(),
                "status",orderRecord.getOrderStatus().name()));


        return orderMapper.toResponse(orderRecord);
    }

    @Override
    public List<PaymentResponse> listPayments(UUID merchantId, UUID orderId) {
        OrderRecord order=orderRepository.findByIdAndMerchantId(orderId,merchantId).orElseThrow(()->new ResourceNotFoundException("Order Not Present with orderId: "+orderId,"Order"));
        List<Payment> paymentList=paymentRepository.findByOrder_Id(orderId);
        return paymentMapper.toResponseList(paymentList);
    }
}
