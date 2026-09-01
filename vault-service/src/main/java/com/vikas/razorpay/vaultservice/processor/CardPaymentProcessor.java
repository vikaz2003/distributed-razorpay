package com.vikas.razorpay.vaultservice.processor;


import com.vikas.razorpay.commonlib.dto.PaymentProcessorRequest;
import com.vikas.razorpay.commonlib.dto.PaymentProcessorResponse;
import com.vikas.razorpay.commonlib.util.RandomizerUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CardPaymentProcessor {


    public static final String PAN_CARD_DECLINED="40000000000002";
    public static final String PAN_CARD_EXPIRED="40000000000001";


    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
       String pan= request.pan();
       if(PAN_CARD_DECLINED.equals(pan)){
           log.info("Card Declined");
           return new PaymentProcessorResponse.Failure("CARD_DECLINED","Card Declined by bank");
       }

        if(PAN_CARD_EXPIRED.equals(pan)){
            log.info("Card Expired");
            return new PaymentProcessorResponse.Failure("CARD_EXPIRED","Card has expired");
        }


        String processorRef="Card_Processor"+ RandomizerUtil.randomBase64(16);


       return new PaymentProcessorResponse.Pending(processorRef);

    }
}
