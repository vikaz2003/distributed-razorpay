package com.vikas.razorpay.merchant_service.service.impl;


import com.vikas.razorpay.commonlib.exception.ResourceNotFoundException;
import com.vikas.razorpay.merchant_service.Entity.Customer;
import com.vikas.razorpay.merchant_service.Entity.Merchant;
import com.vikas.razorpay.merchant_service.repo.CustomerRepository;
import com.vikas.razorpay.merchant_service.repo.MerchantRepository;
import com.vikas.razorpay.merchant_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;

    @Override
    @Transactional
    public UUID findOrCreate(UUID merchantId, String email, String name, String phone) {

        if(email==null||email.isBlank()){
             return null;
        }


        return customerRepository.findByMerchant_IdAndEmail(merchantId,email)
                .map(Customer::getId)
                .orElseGet(()-> createNew(merchantId,email,name,phone));




    }

    @Transactional
    private UUID createNew(UUID merchantId,String email,String name,String phone){
         Merchant merchant=merchantRepository.findById(merchantId)
                 .orElseThrow(()-> new ResourceNotFoundException("Merchant not found with merchantId: ","Merchant"));

         Customer customer=Customer.builder()
                 .merchant(merchant)
                 .email(email)
                 .name(name)
                 .phone(phone)
                 .build();
         customer=customerRepository.save(customer);
         log.info("Customer created via findOrCreate id={} merchantId={} email={} phone={}",customer.getId(),merchantId,email,phone);
         return customer.getId();

    }
}
