package com.vikas.razorpay.commonlib.dto;

public record SettlementBankDetails(

        String accountNumber,
        String ifsc,
        String accountHolderName
) {
}
