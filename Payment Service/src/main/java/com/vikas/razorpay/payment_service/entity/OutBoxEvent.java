package com.vikas.razorpay.payment_service.entity;

import com.vikas.razorpay.commonlib.entity.BaseEntity;
import com.vikas.razorpay.commonlib.enums.EventAggregateType;
import com.vikas.razorpay.commonlib.enums.OutBoxStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutBoxEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventAggregateType aggregateType;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false,columnDefinition = "jsonb")
    private Map<String,Object> payload;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private OutBoxStatus outBoxStatus=OutBoxStatus.PENDING;

    @Column(nullable = false)
    @Builder.Default
    private Integer retries=0;


    @Column(length=1000)
    private String lastError;

    private LocalDateTime publishedAt;





}
