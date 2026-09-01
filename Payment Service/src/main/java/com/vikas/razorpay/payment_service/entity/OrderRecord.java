package com.vikas.razorpay.payment_service.entity;



import com.vikas.razorpay.commonlib.entity.BaseEntity;
import com.vikas.razorpay.commonlib.entity.Money;
import com.vikas.razorpay.commonlib.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name="order_record",
indexes = {
        @Index(name="idx_merchant_id",columnList = "merchant_id"),
        @Index(name="idx_order_status",columnList = "orderStatus")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    // no fk - cross service boundary
    @Column(name="merchant_id",nullable = false)
    private UUID merchantId;


    @Column(name="customer_id",nullable = true)
    private UUID customerId;

    @Embedded
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length=20)
    @Builder.Default
    private OrderStatus orderStatus=OrderStatus.CREATED;

    @Column(length=100)
    private String receipt;

    @Column(nullable=false)
    @Builder.Default
    private Integer attempts = 0;

    @JdbcTypeCode((SqlTypes.JSON))
    @Column(columnDefinition = "jsonb")
    private Map<String,Object> notes;

    @Column(name="expires_at",nullable=false)
    private LocalDateTime expiresAt;

}
