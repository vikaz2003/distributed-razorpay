package com.vikas.razorpay.payment_service.config;



import com.vikas.razorpay.commonlib.enums.PaymentMethod;
import com.vikas.razorpay.payment_service.processor.PaymentProcessor;
import com.vikas.razorpay.payment_service.processor.adapter.CardPaymentProcessor;
import com.vikas.razorpay.payment_service.processor.adapter.NetBankingPaymentProcessor;
import com.vikas.razorpay.payment_service.processor.adapter.UpiPaymentProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentProcessorConfig {

    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessor(){
        return Map.of(
                PaymentMethod.CARD, new CardPaymentProcessor(),
                PaymentMethod.NETBANKING,new NetBankingPaymentProcessor(),
                PaymentMethod.UPI,new UpiPaymentProcessor()
        );
    }
}
