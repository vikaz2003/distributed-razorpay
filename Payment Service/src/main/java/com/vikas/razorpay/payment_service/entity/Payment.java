package com.vikas.razorpay.payment_service.entity;



import com.vikas.razorpay.commonlib.entity.BaseEntity;
import com.vikas.razorpay.commonlib.entity.Money;
import com.vikas.razorpay.commonlib.enums.PaymentMethod;
import com.vikas.razorpay.commonlib.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name="payment",indexes = {
        @Index(name="idx_payment_order_id",columnList = "order_id"),
        @Index(name="idx_payment_merchant_id",columnList = "merchantId")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="order_id",nullable = false)
    private OrderRecord order;
    @Column(nullable = false)
    private UUID merchantId;
    @Embedded
    private Money amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;
    @Column(length = 100)
    private String bankReference;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb",name="method_details")
    private Map<String,Object> methodDetails;
    @Column(length = 100)
    private String errorCode;
    @Column(length=100)
    private String processorReference;
    @Column(length = 255)
    private String errorDescription;
    private LocalDateTime authorizedAt;
    private LocalDateTime capturedAt;
    private LocalDateTime failedAt;
    private LocalDateTime settledAt;
    private LocalDateTime refundedAt;
}
