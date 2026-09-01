package com.vikas.razorpay.commonlib.dto;

import java.util.UUID;

public record PaymentSettlementView(
        UUID paymentId,
        int amountUnits,
        int refundedAmountUnits,
        String currency
) {
}
