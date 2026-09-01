package com.vikas.razorpay.payment_service.api;


import com.vikas.razorpay.commonlib.dto.PaymentSettlementView;
import com.vikas.razorpay.payment_service.entity.Payment;

import java.util.List;
import java.util.UUID;

public interface PaymentLookupService {


   

    List<PaymentSettlementView> findUnSettledCaptured(UUID merchantId);

    void markSettled(List<UUID> paymentIds);
}
