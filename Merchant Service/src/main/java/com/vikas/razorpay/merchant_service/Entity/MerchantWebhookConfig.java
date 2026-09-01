package com.vikas.razorpay.merchant_service.Entity;



import com.vikas.razorpay.commonlib.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "merchant_webhook_config",
indexes = {
        @Index(name = "idx_webhook_merchant_id",columnList = "merchant_id,enabled")
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MerchantWebhookConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;



    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(nullable = false,name ="merchant_id")
    private Merchant merchant;

    @Column(nullable = false,length = 500)
    private String targetUrl; // www.zera.com/webhook/success

    @Column(length = 255)
    private String webhookSecret;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled=true;

    @Column(length = 255)
    private String eventTypes;

    public boolean isSubscribedTo(String eventType){
        if(eventTypes==null||eventTypes.isBlank()){
            return true;
        }
        for(String type:eventTypes.split(",")){
            String trimmed=type.trim();
            if(trimmed.equals("ALL")||trimmed.equalsIgnoreCase(eventType)){
                return true;
            }
        }
        return false;
    }


}
