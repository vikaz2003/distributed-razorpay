package com.vikas.razorpay.payment_service.controller;


import com.vikas.razorpay.commonlib.context.MerchantContext;
import com.vikas.razorpay.payment_service.dto.request.CreateOrderRequest;
import com.vikas.razorpay.payment_service.dto.response.OrderResponse;
import com.vikas.razorpay.payment_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest createOrderRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(merchantContext.getMerchantId(),createOrderRequest));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getByOrderId(@PathVariable UUID orderId){
        return ResponseEntity.ok(orderService.getById(merchantContext.getMerchantId(), orderId));
    }


}
