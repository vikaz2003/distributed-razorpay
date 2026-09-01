package com.vikas.razorpay.operations_service.entity;


import com.vikas.razorpay.commonlib.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "settlementPayment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementPayment extends BaseEntity {


    @EmbeddedId
    private SettlementPaymentId id;

    @MapsId("settlementId")
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="settlement_id",nullable = false)
    private Settlement settlement;


}
