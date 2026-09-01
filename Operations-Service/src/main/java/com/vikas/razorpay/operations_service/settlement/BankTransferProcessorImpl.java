package com.vikas.razorpay.operations_service.settlement;


import com.vikas.razorpay.commonlib.entity.Money;
import com.vikas.razorpay.commonlib.util.RandomizerUtil;
import com.vikas.razorpay.operations_service.settlement.dto.BankTransferResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class BankTransferProcessorImpl implements BankTransferProcessor{


    @Override
    public BankTransferResult initiate(UUID settlementId, UUID merchantId, Money amount, String bankAccount, String ifsc) {
        // call the BANK API

        String registrationRef="TXN"+ RandomizerUtil.randomBase64(12);

        log.debug("Bank Transfer call completed for settlementId:{},registrationRef: {}",settlementId,registrationRef);

        return new BankTransferResult(registrationRef);
    }
}
