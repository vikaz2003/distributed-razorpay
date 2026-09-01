package com.vikas.razorpay.operations_service.settlement;


import com.vikas.razorpay.commonlib.entity.Money;
import com.vikas.razorpay.operations_service.settlement.dto.BankTransferResult;

import java.util.UUID;

public interface BankTransferProcessor {

    BankTransferResult initiate(UUID settlementId, UUID merchantId, Money amount, String bankAccount, String ifsc);
}
