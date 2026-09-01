package com.vikas.razorpay.operations_service.entity;


import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementPaymentId {

    private UUID settlementId;

    private UUID paymentId;

}
