package com.vikas.razorpay.merchant_service.dto.request;



import com.vikas.razorpay.commonlib.enums.Environment;
import lombok.Data;

@Data
public class CreateApiKeyRequest {

    Environment environment;
}
