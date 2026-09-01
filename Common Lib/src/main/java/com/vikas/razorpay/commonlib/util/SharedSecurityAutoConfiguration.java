package com.vikas.razorpay.commonlib.util;


import com.vikas.razorpay.commonlib.config.AesEncryptionConfig;
import com.vikas.razorpay.commonlib.context.MerchantContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.web.context.annotation.RequestScope;

@AutoConfiguration
public class SharedSecurityAutoConfiguration {

    @Bean
    public SignerUtil signerUtil(){
        return new SignerUtil();
    }

    @Bean
    public BytesEncryptor dekEncryptor(@Value("${vault.master-key}") String masterKey){
        return new AesEncryptionConfig().dekEncryptor(masterKey);

    }

    @Bean
    @RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
    public MerchantContext merchantContext(){
        return new MerchantContext();
    }
}


