package com.vikas.razorpay.payment_service.service.impl;

import com.vikas.razorpay.commonlib.dto.PaymentSettlementView;
import com.vikas.razorpay.commonlib.enums.PaymentStatus;
import com.vikas.razorpay.payment_service.api.PaymentLookupService;
import com.vikas.razorpay.payment_service.entity.Payment;
import com.vikas.razorpay.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentLookupServiceImpl implements PaymentLookupService {

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public List<PaymentSettlementView> findUnSettledCaptured(UUID merchantId) {
        List<Payment> paymentList=paymentRepository.findByMerchantIdAndStatusForUpdate(merchantId, PaymentStatus.CAPTURED);
        return paymentList
                .stream()
                .map(
                        p-> new PaymentSettlementView(
                             p.getId(),p.getAmount().getAmountUnits()
                             ,0,p.getAmount().getCurrency()
                        )
                )
                .toList();
    }

    @Override
    @Transactional
    public void markSettled(List<UUID> paymentIds) {
        LocalDateTime now=LocalDateTime.now();
        List<Payment> payments=paymentRepository.findAllById(paymentIds);
        for(Payment payment:payments){

            payment.setStatus(PaymentStatus.SETTLED);
            payment.setSettledAt(now);
        }
        paymentRepository.saveAll(payments);
    }
}
