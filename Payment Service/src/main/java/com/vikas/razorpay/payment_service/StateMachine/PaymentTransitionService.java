package com.vikas.razorpay.payment_service.StateMachine;

import com.vikas.razorpay.commonlib.enums.PaymentActor;
import com.vikas.razorpay.commonlib.enums.PaymentEvent;
import com.vikas.razorpay.commonlib.enums.PaymentStatus;
import com.vikas.razorpay.payment_service.entity.Payment;
import com.vikas.razorpay.payment_service.entity.PaymentTransitionLog;
import com.vikas.razorpay.payment_service.repository.PaymentTransitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionRepository paymentTransitionRepository;
    private final PaymentStateMachine paymentStateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent event) {
        PaymentStatus next = paymentStateMachine.transition(payment.getStatus(), event);
        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .event(event)
                .toStatus(next)
                .actor(PaymentActor.SYSTEM) //TODO: fetch merchant context to identify actor
                .occurredAt(LocalDateTime.now())
                .build();
        payment.setStatus(next);
        paymentTransitionRepository.save(log);
        return next;
    }
}
