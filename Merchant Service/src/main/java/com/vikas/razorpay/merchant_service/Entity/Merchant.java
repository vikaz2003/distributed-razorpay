package com.vikas.razorpay.merchant_service.Entity;



import com.vikas.razorpay.commonlib.entity.BaseEntity;
import com.vikas.razorpay.commonlib.enums.BusinessType;
import com.vikas.razorpay.commonlib.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name="merchant",
indexes = {
        @Index(name = "idx_merchant_status",columnList = "status")
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Merchant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable=false,length=200)
    private String name;

    @Column(unique=true,nullable = false)
    private String email;
    @Column(length=20)
    private String contactNumber;

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;
    @Column(length=100)
    private String businessName;
    @Column(length=200)
    private String websiteUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MerchantStatus status=MerchantStatus.PENDING_KYC;
    private String gstId;
    private String panId;

    @Column(length=200)
    private String settlementBankAccount;
    @Column(length=20)
    private String settlementBankIfsc;
    @Column(length=200)
    private String settlementBankAccountHolderName;


}
