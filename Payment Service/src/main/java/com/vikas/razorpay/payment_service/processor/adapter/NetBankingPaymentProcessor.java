package com.vikas.razorpay.payment_service.processor.adapter;


import com.vikas.razorpay.commonlib.util.RandomizerUtil;
import com.vikas.razorpay.payment_service.processor.PaymentProcessor;
import com.vikas.razorpay.payment_service.processor.dto.PaymentProcessorRequest;
import com.vikas.razorpay.payment_service.processor.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

@Component
public class NetBankingPaymentProcessor implements PaymentProcessor {


    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

      final String BANK_CODE_FAIL="BANK_CODE_FAIL";

        // Call the third party
        String bankCode=request.methodDetails()!=null?
                request.methodDetails().get("BANK").toString():BANK_CODE_FAIL;

        if(BANK_CODE_FAIL.equals(bankCode)){
            return new PaymentProcessorResponse.Failure("BANK_REJECTED","Bank rejected the transaction registration");
        }

        String processorRef="NBK_PROCESSOR"+ RandomizerUtil.randomBase64(16);
        String redirectRef="http://REDIRECT.BANK.com/"+processorRef;


        return new PaymentProcessorResponse.Success(processorRef,redirectRef);
    }
}
